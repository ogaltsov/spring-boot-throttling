## Throttling functionality
<p>
Spring Boot application that contains one controller with a single method, which returns HTTP 200 and an empty body.
</p>
The number of requests from a single IP address to this method is limited to N requests in X minutes(based on configuration file).
<p>
If the number of requests exceeds this limit, a 502 error will be returned until the number of requests in the given interval drops below N.
<p>
This quote restriction can be quickly applied to new methods by using @Quoted annotation.
<p>
The implementation supports a multi-threaded and high-load execution environment.

## Implementation
![vis](https://raw.githubusercontent.com/ogaltsov/amzscout-test-task/main/Algorithm.jpeg)
The users resource request is stored on a timeline (queue with timestamps).
<p>
Algorithm uses CircularFifoQueue, which delete oldest elements if queue reaches max capacity - so there is no need to take care about cleaning old data.
<p>
The user-resource quota is calculated using the fixed-length Sliding Window Algorithm.
