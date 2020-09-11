# HOW MAP-REDUCE WORKS ON YARN - 

* You can run a MapReduce job with a single method call: submit() on a Job object (you can also call waitForCompletion(), which submits the job if it hasn’t been submitted already, then waits for it to finish).1 This method call conceals a great deal of processing behind the scenes.

* 5 independent entities of the YARN -
	* Client which submits the MR job
	* The YARN resource manager, which coordinates the allocation of compute resources on the cluster.
	* The YARN node managers, which launch and monitor the compute containers on machines in the cluster.
	* The MapReduce application master, which coordinates the tasks running the Map‐Reduce job. The application master and the MapReduce tasks run in containers that are scheduled by the resource manager and managed by the node managers.
	* HDFS - underlying storage system.

* Job Submission - The submit() method on Job creates an internal JobSubmitter instance and calls submitJobInternal() on it. Having submitted the job, waitForCompletion() polls the job’s progress once per second and reports the progress to the
console if it has changed since the last report. When the job completes successfully, the job counters are displayed. Otherwise, the error that caused the job to fail is logged to the console.

* The job submission process implemented by JobSubmitter does the following:
	* Asks the resource manager for a new application ID, used for the MapReduce job ID
	* Checks the output specification of the job. For example, if the output directory has not been specified or it already exists, the job is not submitted and an error is thrown to the MapReduce program.
	* Computes the input splits for the job. If the splits cannot be computed (because the input paths don’t exist, for example), the job is not submitted and an error is thrown to the MapReduce program.
	* Copies the resources needed to run the job, including the job JAR file, the configuration file, and the computed input splits, to the shared filesystem in a directory named after the job ID (step 3). The job JAR is copied with a high replication factor (controlled by the mapreduce.client.submit.file.replication property, which defaults to 10) so that there are lots of copies across the cluster for the node managers to access when they run tasks for the job.
	* Submits the job by calling submitApplication() on the resource manager

* Job Initialization - 
	* When the resource manager receives a call to its submitApplication() method, it hands off the request to the YARN scheduler. The scheduler allocates a container, and the resource manager then launches the application master’s process there, under the node manager’s management
	* MapReduce Application Master tracks the Job's progress.
	* Next, it retrieves the input splits computed in the client from the shared filesystem
	* It creates the Map Tasks and the Reduce Tasks and assigns the Task IDs to them.
	* By default, a small job is one that has less than 10 mappers, only one reducer, and an input size that is less than the size of one HDFS block. If Application Master receives a small job then it might execute it in the same JVM as itself in parallel. Such a job is said to be uberized, or run as an uber task.
	* Uber tasks must be enabled explicitly (for an individual job, or across the cluster) by setting mapreduce.job.ubertask.enable to true.
	* Finally, before any tasks can be run, the application master calls the setupJob() method on the OutputCommitter. For FileOutputCommitter, which is the default, it will create the final output directory for the job and the temporary working space for the task output.

* Task Assignment - 
	* If the job is not an uber job, then application master asks for containers from the resource manager for executing the MapReduce Tasks.
	* Requests for map tasks are made first and with a higher priority than those for reduce tasks, since all the map tasks must complete before the sort phase of the reduce can start. Requests for reduce tasks are not made until 5% of map tasks have completed. Reduce tasks can run anywhere in the cluster, but requests for map tasks have data locality constraints that the scheduler tries to honor. 
	* In the optimal case, the task is data local—that is, running on the same node that the split resides on. Alternatively, the task may be rack local: on the same rack, but not the same node, as the split. Some tasks are either data local nor rack local and retrieve their data from a different rack than the one they are running on. For a particular job run, you can determine the number of tasks that ran at each locality level by looking at the job’s counters.
	* Requests also specify memory requirements and CPUs for tasks. By default, each map and reduce task is allocated 1,024 MB of memory and one virtual core. The values are configurable on a per-job basis. These values can be changes via setting the following properties - 
		* mapreduce.map.memory.mb, 
		* mapreduce.reduce.memory.mb, 
		* mapreduce.map.cpu.vcores 
		* mapreduce.reduce.cpu.vcores.

* Task Execution - 
	* Task is assigned resources for a container on a particular node by resource manager's Scheduler. The application master starts the container by contacting the node manager. The task is executed by a Java application whose main class is YarnChild. Before it can run the task, it localizes the resources that the task needs, including the job configuration and JAR file, and any files from the distributed cache.
	* The YarnChild runs in a dedicated JVM, so that any bugs in the user-defined map and reduce functions don’t affect the node manager—by causing it to crash or hang, for example.

* Streaming - Streaming runs special map and reduce tasks for the purpose of launching the user-supplied executable and communicating with it.

* Progress and Status Updates - 
	* When a task is running, it keeps track of its progress (i.e., the proportion of the task completed). For map tasks, this is the proportion of the input that has been processed. For reduce tasks, it’s a little more complex, but the system can still estimate the proportion of the reduce input processed. Tasks also have a set of counters that count various events as the task runs.
	* As the map or reduce task runs, the child process communicates with its parent application master through the umbilical interface. The task reports its progress and status (including counters) back to its application master, which has an aggregate view of the job, every three seconds over the umbilical interface.
	* During the course of the job, the client receives the latest status by polling the application master every second (the interval is set via mapreduce.client.progressmonitor.pollinterval). Clients can also use Job’s getStatus() method to obtain a JobStatus instance, which contains all of the status information for the job.

* Job Completion - 
	* When the application master receives a notification that the last task for a job is complete, it changes the status for the job to “successful.” Then, when the Job polls for status, it learns that the job has completed successfully, so it prints a message to tell the user and then returns from the waitForCompletion() method. Job statistics and counters are printed to the console at this point.
	* The application master also sends an HTTP job notification if it is configured to do so. This can be configured by clients wishing to receive callbacks, via the mapreduce.job.end-notification.url property.
	* Finally, on job completion, the application master and the task containers clean up their working state (so intermediate output is deleted), and the OutputCommitter’s commit Job() method is called. Job information is archived by the job history server to enable later interrogation by users if desired.

* Failures - 
	* Hanging tasks are dealt with differently. The application master notices that it hasn’t received a progress update for a while and proceeds to mark the task as failed. The task JVM process will be killed automatically after this period.3 The timeout period after which tasks are considered failed is normally 10 minutes and can be configured on a per-job basis (or a cluster basis) by setting the mapreduce.task.timeout property to a value in milliseconds.
	* When the application master is notified of a task attempt that has failed, it will reschedule execution of the task. The application master will try to avoid rescheduling the task on a node manager where it has previously failed. Furthermore, if a task fails four times, it will not be retried again. This value is configurable. The maximum number of attempts to run a task is controlled by the mapreduce.map.maxattempts property for map tasks and mapreduce.reduce.maxattempts for reduce tasks. By default, if any task fails four times (or whatever the maximum number of attempts is configured to), the whole job fails.
	* For some applications, it is undesirable to abort the job if a few tasks fail, as it may be possible to use the results of the job despite some failures. In this case, the maximum percentage of tasks that are allowed to fail without triggering job failure can be set for the job. Map tasks and reduce tasks are controlled independently, using the mapreduce.map.failures.maxpercent and mapreduce.reduce.failures.maxpercent properties.

* Application Master Failure - 
	* The maximum number of attempts to run a MapReduce application master is controlled by the mapreduce.am.max-attempts property. The default value is 2, so if a MapReduce application master fails twice it will not be tried again and the job will fail.
	* YARN imposes a limit for the maximum number of attempts for any YARN application master running on the cluster, and individual applications may not exceed this limit. The limit is set by yarn.resourcemanager.am.max-attempts and defaults to 2, so if you want to increase the number of MapReduce application master attempts, you will have to increase the YARN setting on the cluster, too.
	* The way recovery works is as follows. An application master sends periodic heartbeats to the resource manager, and in the event of application master failure, the resource manager will detect the failure and start a new instance of the master running in a new container (managed by a node manager).
	* In the case of the MapReduce application master, it will use the job history to recover the state of the tasks that were already run by the (failed) application so they don’t have to be rerun. Recovery is enabled by default, but can be disabled by setting yarn.app.mapreduce.am.job.recovery.enable to false.
	* The MapReduce client polls the application master for progress reports, but if its application master fails, the client needs to locate the new instance. During job initialization, the client asks the resource manager for the application master’s address, and then caches it so it doesn’t overload the resource manager with a request every time it needs to poll the application master. If the application master fails, however, the client will experience a timeout when it issues a status update, at which point the client will go back to the resource manager to ask for the new application master’s address. This process is transparent to the user.

* Node Manager Failure - 
	* If a node manager fails by crashing or running very slowly, it will stop sending heartbeats to the resource manager (or send them very infrequently). The resource manager will notice a node manager that has stopped sending heartbeats if it hasn’t received one for 10 minutes (this is configured, in milliseconds, via the yarn.resourcemanager.nm.liveness-monitor.expiry-interval-ms property) and remove it from its pool of nodes to schedule containers on.
	* Node managers may be blacklisted if the number of failures for the application is high, even if the node manager itself has not failed. Blacklisting is done by the application master, and for MapReduce the application master will try to reschedule tasks on different nodes if more than three tasks fail on a node manager. The user may set the threshold with the mapreduce.job.maxtaskfailures.per.tracker job property.
	* Note that the resource manager does not do blacklisting across applications (at the time of writing), so tasks from new jobs may be scheduled on bad nodes even if they have been blacklisted by an application master running an earlier job

* Resource Manager Failure -
	* Note that the resource manager does not do blacklisting across applications (at the time of writing), so tasks from new jobs may be scheduled on bad nodes even if they have been blacklisted by an application master running an earlier job.
	* To achieve high availability (HA), it is necessary to run a pair of resource managers in an active-standby configuration. If the active resource manager fails, then the standby can take over without a significant interruption to the client.
	* Information about all the running applications is stored in a highly available state store (backed by ZooKeeper or HDFS), so that the standby can recover the core state of the failed active resource manager. Node manager information is not stored in the state store since it can be reconstructed relatively quickly by the new resource manager as the node managers send their first heartbeats. (Note also that tasks are not part of the resource manager’s state, since they are managed by the application master. Thus, the amount of state to be stored is therefore much more manageable than that of the jobtracker in MapReduce 1.)
	* When the new resource manager starts, it reads the application information from the state store, then restarts the application masters for all the applications running on the cluster. This does not count as a failed application attempt (so it does not count against yarn.resourcemanager.am.max-attempts), since the application did not fail due to an error in the application code, but was forcibly killed by the system.
	* The transition of a resource manager from standby to active is handled by a failover controller. The default failover controller is an automatic one, which uses ZooKeeper leader election to ensure that there is only a single active resource manager at one time.
	* The failover controller does not have to be a standalone process, and is embedded in the resource manager by default for ease of configuration. It is also possible to configure manual failover, but this is not recommended.
	* Clients and node managers must be configured to handle resource manager failover, since there are now two possible resource managers to communicate with. They try connecting to each resource manager in a round-robin fashion until they find the active one. If the active fails, then they will retry until the standby becomes active.

* Shuffle and sort - (This section talks about the Shuffle and sort functionality in depth for the MR jobs)
	* MapReduce makes the guarantee that the input to every reducer is sorted by key. The process by which the system performs the sort—and transfers the map outputs to the reducers as inputs—is known as the shuffle.
	* Each map task has a circular memory buffer that it writes the output to. The buffer is 100 MB by default (the size can be tuned by changing the mapreduce.task.io.sort.mb property). When the contents of the buffer reach a certain threshold size (mapreduce.map.sort.spill.percent, which has the default value 0.80, or 80%), a background thread will start to spill the contents to disk. Map outputs will continue to be written to the buffer while the spill takes place, but if the buffer fills up during this time, the map will block until the spill is complete. Spills are written in round-robin fashion to the directories specified by the mapreduce.cluster.local.dir property, in a jobspecific subdirectory.
	* Running the combiner function makes for a more compact map output, so there is less data to write to local disk and to transfer to the reducer.
