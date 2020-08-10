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
