package com.demo.oragejobsite.controller;
import com.demo.oragejobsite.entity.ApplyJob;
import com.demo.oragejobsite.entity.SavedJob;
import com.demo.oragejobsite.dao.ApplyDao;
import com.demo.oragejobsite.dao.SavedJobDao;
import com.demo.oragejobsite.entity.Employer;
import com.demo.oragejobsite.entity.Follow;
import com.demo.oragejobsite.entity.PostJob;
import com.demo.oragejobsite.dao.EmployerDao;
import com.demo.oragejobsite.dao.FollowRepository;
import com.demo.oragejobsite.dao.PostjobDao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;





@CrossOrigin(origins = "${myapp.url}")
@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private EmployerDao employerDao;
    
    @Autowired
    private PostjobDao postjobDao;



	@Autowired
	private ApplyDao apd;
	  @Autowired
	  private SavedJobDao savedJobServiceimpl;
    
    
    @GetMapping
    public ResponseEntity<?> getAllFollows() {
        try {
            List<Follow> follows = followRepository.findAll();
            return ResponseEntity.ok(follows);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching all follows: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFollowById(@PathVariable String id) {
        try {
            return followRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follow by ID: " + e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<?> toggleFollow(@RequestBody Follow follow) {
        try {
            List<Follow> existingFollows = followRepository.findByUidAndEmpid(follow.getUid(), follow.getEmpid());
            if (!existingFollows.isEmpty()) {
                followRepository.deleteAll(existingFollows);
                return ResponseEntity.status(HttpStatus.OK).body("Unfollowed successfully.");
            } else {
                Follow savedFollow = followRepository.save(follow);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedFollow);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the follow/unfollow action: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFollow(@PathVariable String id, @RequestBody Follow followDetails) {
        try {
            return followRepository.findById(id)
                    .map(follow -> {
                        follow.setUid(followDetails.getUid());
                        follow.setEmpid(followDetails.getEmpid());
                        follow.setSendTime(followDetails.getSendTime());
                        follow.setFollowing(followDetails.isFollowing());
                        Follow updatedFollow = followRepository.save(follow);
                        return ResponseEntity.ok(updatedFollow);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the follow record: " + e.getMessage());
        }
    }




    @GetMapping("/byuid")
    public ResponseEntity<?> getFollowsByUid(
            @RequestParam String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        try {
            // Create pageable object for pagination
            Pageable pageable = PageRequest.of(page, size);

            // Fetch page of follows for the given UID
            Page<Follow> followPage = followRepository.findByUid(uid, pageable);

            // Check if page is empty
            if (followPage.isEmpty()) {
                // Calculate total items and total pages based on all follows for the UID
                long totalItems = followRepository.countByUid(uid);
                int totalPages = (int) Math.ceil((double) totalItems / size);

                // If requested page is out of bounds, return an empty result with pagination info
                if (page >= totalPages) {
                    Map<String, Object> emptyResponse = new HashMap<>();
                    emptyResponse.put("followers", Collections.emptyList());
                    emptyResponse.put("currentPage", page);
                    emptyResponse.put("totalItems", totalItems);
                    emptyResponse.put("totalPages", totalPages);
                    return ResponseEntity.ok(emptyResponse);
                } else {
                    // Otherwise, return NOT_FOUND indicating no follows found for the given page
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given page number.");
                }
            }

            // Prepare response with followers and pagination info
            Map<String, Object> response = new HashMap<>();
            response.put("followers", followPage.getContent());
            response.put("currentPage", followPage.getNumber());
            response.put("totalItems", followPage.getTotalElements());
            response.put("totalPages", followPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follows by UID: " + e.getMessage());
        }
    }



    @GetMapping("/emp/{empid}")
    public ResponseEntity<?> getFollowsByEmpid(
            @PathVariable String empid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size) {
        try {
            // Create pageable object for pagination
            Pageable pageable = PageRequest.of(page, size);

            // Fetch page of follows for the given empid
            Page<Follow> followPage = followRepository.findByEmpid(empid, pageable);

            // Check if page is empty
            if (followPage.isEmpty()) {
                // Calculate total items for the empid
                long totalItems = followRepository.countByEmpid(empid);
                int totalPages = (int) Math.ceil((double) totalItems / size);

                // If requested page is out of bounds, return an empty result with pagination info
                if (page >= totalPages) {
                    Map<String, Object> emptyResponse = new HashMap<>();
                    emptyResponse.put("followers", Collections.emptyList());
                    emptyResponse.put("currentPage", page);
                    emptyResponse.put("totalItems", totalItems);
                    emptyResponse.put("totalPages", totalPages);
                    return ResponseEntity.ok(emptyResponse);
                } else {
                    // Otherwise, return NOT_FOUND indicating no follows found for the given page
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given empid.");
                }
            }

            // Prepare response with followers and pagination info
            Map<String, Object> response = new HashMap<>();
            response.put("followers", followPage.getContent());
            response.put("currentPage", followPage.getNumber());
            response.put("totalItems", followPage.getTotalElements());
            response.put("totalPages", followPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follows by empid: " + e.getMessage());
        }
    }


    
    @GetMapping("/count/unique-empid")
    public ResponseEntity<?> getCountOfUniqueEmpids(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Count unique empids followed by the user
            long uniqueEmpidCount = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .count();

            return ResponseEntity.ok(uniqueEmpidCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while counting unique empids: " + e.getMessage());
        }
    }

    
    @GetMapping("/unique-empids")
    public ResponseEntity<?> getUniqueEmpIdsByUid(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Get unique empids followed by the user
            List<String> uniqueEmpIds = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(uniqueEmpIds);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving unique empids: " + e.getMessage());
        }
    }
    
    @GetMapping("/employer-data")
    public ResponseEntity<?> getEmployerDataByUid(
            @RequestParam String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String empcompany,
            @RequestParam(required = false) String empcountry) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Follow> followsPage = followRepository.findByUid(uid, pageable);

            // Fetch all unique employer IDs followed by the user
            List<String> uniqueEmpIds = followRepository.findByUid(uid).stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .collect(Collectors.toList());

            // Retrieve employer details and filter based on optional parameters
            List<Employer> employers = uniqueEmpIds.stream()
                    .map(empId -> employerDao.findByEmpid(empId).orElse(null))
                    .filter(Objects::nonNull)
                    .filter(emp -> empcompany == null || (emp.getEmpcompany() != null && emp.getEmpcompany().toLowerCase().contains(empcompany.toLowerCase())))
                    .filter(emp -> empcountry == null || (emp.getEmpcountry() != null && emp.getEmpcountry().toLowerCase().contains(empcountry.toLowerCase())))
                    .collect(Collectors.toList());

            // Implement pagination for employers
            int start = Math.min(page * size, employers.size());
            int end = Math.min((page + 1) * size, employers.size());
            List<Employer> paginatedEmployers = employers.subList(start, end);

            // Create a response map including pagination details
            Map<String, Object> response = new HashMap<>();
            response.put("employers", paginatedEmployers);
            response.put("currentPage", page);
            response.put("totalItems", employers.size());
            response.put("totalPages", (int) Math.ceil((double) employers.size() / size));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving employer data: " + e.getMessage());
        }
    }


    
  
    @GetMapping("/approved-postjobs")
    public ResponseEntity<?> getApprovedPostJobsByUidAndEmpid(
            @RequestParam String uid,
            @RequestParam String empid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String locationjob,
            @RequestParam(required = false) String jobtitle) {
        try {
            // Check if the user follows the given empid
            List<Follow> follows = followRepository.findByUidAndEmpid(uid, empid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user does not follow the specified employee.");
            }

            // Get the follow record for the given uid and empid
            Follow follow = follows.get(0); // Assuming only one follow record per (uid, empid) pair

            // Create a Pageable object for pagination
            Pageable pageable = PageRequest.of(page, size);

            // Fetch jobs based on filters and add pagination
            Page<PostJob> jobsPage;
            if (locationjob != null && !locationjob.isEmpty() && jobtitle != null && !jobtitle.isEmpty()) {
                jobsPage = postjobDao.findByEmpidAndApprovejobAndLocationjobContainingIgnoreCaseAndJobtitleContainingIgnoreCaseAndSendTimeAfter(
                        empid, true, locationjob, jobtitle, follow.getSendTime(), pageable);
            } else if (locationjob != null && !locationjob.isEmpty()) {
                jobsPage = postjobDao.findByEmpidAndApprovejobAndLocationjobContainingIgnoreCaseAndSendTimeAfter(
                        empid, true, locationjob, follow.getSendTime(), pageable);
            } else if (jobtitle != null && !jobtitle.isEmpty()) {
                jobsPage = postjobDao.findByEmpidAndApprovejobAndJobtitleContainingIgnoreCaseAndSendTimeAfter(
                        empid, true, jobtitle, follow.getSendTime(), pageable);
            } else {
                jobsPage = postjobDao.findByEmpidAndApprovejobAndSendTimeAfter(
                        empid, true, follow.getSendTime(), pageable);
            }

            List<PostJob> allPostJobs = jobsPage.getContent();
            long totalItems = jobsPage.getTotalElements();

            // Combine approvedPostJobs with allPostJobs while avoiding duplicates
            Set<PostJob> combinedPostJobs = new HashSet<>(allPostJobs);

            // Create a list of job post maps to include saveStatus and appliedJob
            List<Map<String, Object>> jobPostMaps = new ArrayList<>();

            for (PostJob postJob : combinedPostJobs) {
                Map<String, Object> jobPostMap = new HashMap<>();
                jobPostMap.put("postJob", postJob);

                // Add saveStatus and appliedJob
                if (uid != null) {
                    SavedJob savedJob = savedJobServiceimpl.findByJobidAndUid(postJob.getJobid(), uid);
                    boolean saveStatus = (savedJob != null) && savedJob.getSaveStatus();
                    jobPostMap.put("saveStatus", saveStatus);

                    ApplyJob appliedJob = apd.findByJobidAndUid(postJob.getJobid(), uid);
                    boolean appliedJobStatus = (appliedJob != null);
                    jobPostMap.put("appliedJob", appliedJobStatus);
                }

                jobPostMaps.add(jobPostMap);
            }

            // Create response map
            Map<String, Object> response = new HashMap<>();
            response.put("jobPosts", jobPostMaps);
            response.put("currentPage", jobsPage.getNumber());
            response.put("totalItems", totalItems);
            response.put("totalPages", jobsPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving approved post jobs: " + e.getMessage());
        }
    }



    @GetMapping("/posted-job-count")
    public ResponseEntity<?> getPostedJobCountByUid(@RequestParam String uid) {
        try {
            // Get follows by the user
            List<Follow> follows = followRepository.findByUid(uid);

            // Check if follows list is empty
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given uid.");
            }

            // Group follows by empid and get the latest sendTime for each empid
            Map<String, Date> empidToSendTimeMap = follows.stream()
                    .collect(Collectors.toMap(
                            Follow::getEmpid,
                            Follow::getSendTime,
                            (oldValue, newValue) -> newValue.after(oldValue) ? newValue : oldValue
                    ));

            // Get the count of approved jobs for each unique empid where sendTime > follow's sendTime
            long totalApprovedJobCount = empidToSendTimeMap.entrySet().stream()
                    .mapToLong(entry -> {
                        String empid = entry.getKey();
                        Date followSendTime = entry.getValue();
                        return postjobDao.findByEmpidAndApprovejobTrue(empid).stream()
                                .filter(postJob -> postJob.getSendTime().before(followSendTime))
                                .count();
                    })
                    .sum();

            return ResponseEntity.ok(totalApprovedJobCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the posted job count: " + e.getMessage());
        }
    }

    
    @GetMapping("/follow-count")
    public ResponseEntity<?> getFollowCountByEmpid(@RequestParam String empid) {
        try {
            List<Follow> follows = followRepository.findByEmpid(empid);
            long followCount = follows.size();
            return ResponseEntity.ok(followCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follow count for empid: " + empid + ". Error: " + e.getMessage());
        }
    }


    
    
}
