### Important points from - HADOOP DEFINITIVE GUIDE 3RD EDITION

##### CHAPTER - 1 - MEET HADOOP

* The amount of data is increasing. BIGDATA has arrived and hence we need a good framework to store and analyze the data efficiently.
* Reading and writing the data in parallel would speed up our application.
* Hadoop provides reliable shared storage and analysis. The storage is provided by HDFS and analysis by MapReduce.
* MapReduce is a batch query processor, and the ability to run an adhoc query against your whole dataset and get the results in a reasonable time is transformative.
* Mapreduce is suitable for the applications where is the data is written once and read many times. Also, Mapreduce works with unstructured and semi-structured data also. Mapreduce is a linearly scalable programming model.
* Data Locality - In Hadoop, Data locality is the process of moving the computation close to where the actual data resides on the node, instead of moving large data to computation.
* MapReduce is able to do this because it is a shared-nothing architecture, meaning that tasks have no dependence on one other.
* Hadoop was created by Doug Cutting, the creator of Apache Lucene, the widely used text search library. Hadoop has its origins in Apache Nutch, an open source web search engine, itself a part of the Lucene project.
* There are Hadoop distributions from the large, established enterprise vendors, including EMC, IBM, Microsoft, and Oracle, as well as from specialist Hadoop companies such as Cloudera, Hortonworks, and MapR.
* Hadoop ecosystem consists of - HDFS, MapReduce, Avro, Pig, Hive, HBase, Sqoop, OOzie, Zookeeper.
* Hadoop 2.x includes YARN.

##### CHAPTER - 2 - MAPREDUCE

* MapReduce is a programming model for data processing. MapReduce has two phases - Map Phase and Reduce Phase. The programmer also specifies two functions: the map function and the reduce function.
* The output from the map function is processed by the MapReduce framework before being sent to the reduce function. This processing sorts and groups the key-value pairs by key.
* Working of mapreduce is defined. Also, the differences between the old API and new API are defined.
* A MapReduce job is a unit of work that the client wants to be performed: it consists of the input data, the MapReduce program, and configuration information. Hadoop runs the job by dividing it into tasks, of which there are two types:
map tasks and reduce tasks.
* Hadoop divides the input to a MapReduce job into fixed-size pieces called input splits, or just splits. Hadoop creates one map task for each split, which runs the userdefined map function for each record in the split.
* Having many splits means the time taken to process each split is small compared to the time to process the whole input. So if we are processing the splits in parallel, the processing is better load-balanced when the splits are small, since a faster machine will be able to process proportionally more splits over the course of the job than a slower
machine. Even if the machines are identical, failed processes or other jobs running concurrently make load balancing desirable, and the quality of the load balancing increases as the splits become more fine-grained.
* On the other hand, if splits are too small, the overhead of managing the splits and of map task creation begins to dominate the total job execution time. For most jobs, a good split size tends to be the size of an HDFS block, 128 MB by default, although this can be changed for the cluster (for all newly created files) or specified when each file is
created.
* Hadoop does its best to run the map task on a node where the input data resides in HDFS. This is called the data locality optimization because it doesn’t use valuable cluster bandwidth. Sometimes, however, all three nodes hosting the HDFS block replicas for a map task’s input split are running other map tasks, so the job scheduler will look
for a free map slot on a node in the same rack as one of the blocks. Very occasionally even this is not possible, so an off-rack node is used, which results in an inter-rack network transfer.
* It should now be clear why the optimal split size is the same as the block size: it is the largest size of input that can be guaranteed to be stored on a single node. If the split spanned two blocks, it would be unlikely that any HDFS node stored both blocks, so some of the split would have to be transferred across the network to the node running the map task, which is clearly less efficient than running the whole map task using local data.
* Map tasks write their output to the local disk, not to HDFS. Why is this? Map output is intermediate output: it’s processed by reduce tasks to produce the final output, and once the job is complete, the map output can be thrown away. So storing it in HDFS with replication would be overkill. If the node running the map task fails before the map output has been consumed by the reduce task, then Hadoop will automatically rerun the map task on another node to re-create the map output.
* Reduce tasks don’t have the advantage of data locality; the input to a single reduce task is normally the output from all mappers. In the present example, we have a single reduce task that is fed by all of the map tasks. Therefore, the sorted map outputs have to be transferred across the network to the node where the reduce task is running, where
they are merged and then passed to the user-defined reduce function. The output of the reduce is normally stored in HDFS for reliability.
* Each HDFS block of the reduce output, the first replica is stored on the local node, with other replicas being stored on off-rack nodes. Thus, writing the reduce output does consume network bandwidth, but only as much as a normal HDFS write pipeline consumes. The number of reduce tasks is not governed by the size of the input, but instead is specified independently.
* When there are multiple reducers, the map tasks partition their output, each creating one partition for each reduce task. There can be many keys (and their associated values) in each partition, but the records for any given key are all in a single partition. The partitioning can be controlled by a user-defined partitioning function, but normally the
default partitioner—which buckets keys using a hash function—works very well.
* The data flow between map and reduce task is know as "Shuffle". It is possible to have zero reduce tasks.
* Many MapReduce jobs are limited by the bandwidth available on the cluster, so it pays to minimize the data transferred between map and reduce tasks. Hadoop allows the user to specify a combiner function to be run on the map output, and the combiner function’s output forms the input to the reduce function. Because the combiner function is an optimization, Hadoop does not provide a guarantee of how many times it will call it for a particular map output record, if at all. In other words, calling the combiner function zero, one, or many times should produce the same output from the reducer.
* The combiner function doesn’t replace the reduce function. (How could it? The reduce function is still needed to process records with the same key from different maps.) But it can help cut down the amount of data shuffled between the mappers and the reducers, and for this reason alone it is always worth considering whether you can use a combiner
function in your MapReduce job.
* Hadoop Streaming is suited for text processing. Hadoop Pipes is the name of the C++ interface to Hadoop MapReduce. Unlike Streaming, which uses standard input and output to communicate with the map and reduce code, Pipes uses sockets as the channel over which the tasktracker communicates with the process running the C++ map or reduce function.
* This chapter also describes how we can write a MapReduce program using Ruby, Python and C++.
