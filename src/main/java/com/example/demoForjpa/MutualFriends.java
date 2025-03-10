Phase 1: Strengthen Core Java (2-3 Weeks)
Java Basics

Data types, variables, operators
Control flow (if-else, loops, switch)
Methods and recursion
Object-Oriented Programming (OOP)

Classes, objects, constructors
Inheritance, polymorphism, encapsulation, abstraction
Interfaces and abstract classes
Exception Handling & Debugging

Try-catch-finally, throws, throw
Custom exceptions
Debugging techniques
Collections Framework

List, Set, Queue, Map
HashMap, TreeMap, HashSet, LinkedList
Comparable vs Comparator
Multithreading & Concurrency

Thread lifecycle
Synchronization, volatile, locks
Executors, thread pools, CompletableFuture
Phase 2: Advanced Java (3-4 Weeks)
Java 8+ Features

Lambda expressions
Streams API
Functional interfaces
Optional class
JVM & Performance Optimization

JVM architecture, garbage collection
Memory management, stack vs heap
Profiling tools (JVisualVM, JConsole)
File Handling & I/O

FileReader, BufferedReader
Serialization & Deserialization
NIO (New Input/Output)
Database Connectivity (JDBC, ORM)

JDBC basics, prepared statements
Hibernate, JPA basics
Caching, transactions
Phase 3: Spring Framework & Backend Development (4-5 Weeks)
Spring Core & Spring Boot

Dependency Injection (DI), IoC Container
Application Context, Bean scopes
Spring Boot starters
Spring MVC & REST APIs

Creating REST APIs with Spring Boot
Request mappings, exception handling
Authentication & JWT
Spring Data & Hibernate

JPA repositories, Query methods
Relationships (OneToMany, ManyToOne)
Microservices & Cloud

Spring Cloud, Eureka, Feign Client
API Gateway, Circuit Breaker
Docker & Kubernetes basics
Phase 4: Data Structures & Algorithms (5-6 Weeks)
Arrays, Strings, Linked Lists
Stacks, Queues, Hashing
Recursion & Dynamic Programming
Binary Search Trees, Graphs
Sorting & Searching Algorithms
Practice on Leetcode (Medium-Hard), HackerRank, CodeForces

Phase 5: System Design & Interview Preparation (4-6 Weeks)
Low-Level Design (LLD)

Design Patterns (Singleton, Factory, Observer)
SOLID principles
High-Level Design (HLD)

Scalability, Load Balancing
Caching, Database Sharding
Mock Interviews & Practice

Solve at least 2 coding problems daily
Mock interviews on Pramp, InterviewBit
Resources & Roadmap
✅ Books: "Effective Java" by Joshua Bloch, "Java: The Complete Reference"
✅ Courses: Java Mastery (Udemy), DSA in Java (NeetCode, CodeWithHarry)
✅ Practice: Leetcode, CodeChef, GeeksforGeeks
✅ Projects: Build 2-3 full-stack applications

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import java.util.*;
public class MutualFriends{

	public static class Map 
			extends Mapper<LongWritable, Text, Text, Text>{
		Text user = new Text();
		Text friends = new Text();
		
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException{
			/*Splitting each line into user and corresponding friend list*/
			String[] split=value.toString().split("\\t");
			/*split[0] is user and split[1] is friend list */
			String userId=split[0];
			if(split.length==1) {
				return;
			}
			String[] friendIds=split[1].split(",");
			for(String friend : friendIds) {
				if(userId.equals(friend)) {
					continue;
				}
			String userKey = (Integer.parseInt(userId) < Integer.parseInt(friend))?userId + "," +friend : friend + ","+ userId;
			String regex="((\\b"+ friend + "[^\\w]+)|\\b,?" + friend + "$)";
			friends.set(split[1].replaceAll(regex, ""));
			user.set(userKey);
			context.write(user,friends);
			}
		}
	}
		
	public static class Reduce
			extends Reducer<Text, Text, Text, Text>{
		
		private String matchingFriends(String firstList, String secondList) {
			
			if(firstList == null || secondList == null) {
				return null;
			}
			
			String[] list1=firstList.split(",");
			String[] list2=secondList.split(",");
			
			LinkedHashSet<String> firstSet = new LinkedHashSet<String>();
			for(String  user: list1) {
				firstSet.add(user);
			}
			/*Retaining sort order*/
			LinkedHashSet<String> secondSet = new LinkedHashSet<String>();
			for(String  user: list2) {
				secondSet.add(user);
			}
			firstSet.retainAll(secondSet);
			/*Keeping only the matched friends*/
			return firstSet.toString().replaceAll("\\[|\\]", "");
		}
		
		public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException,InterruptedException{
			
			String[] friendsList = new String[2];
			int index=0;
			
			for(Text value:values) {
				friendsList[index++] = value.toString();
			}
			String mutualFriends = matchingFriends(friendsList[0],friendsList[1]);
			if(mutualFriends != null && mutualFriends.length() != 0) {
				context.write(key, new Text(mutualFriends));
			}
		}
		
	}

	


	// Driver program
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: MutualFriends <input file name> <output file name>");
			System.exit(2);
		}

		// create a job with name "mutual friends"
		@SuppressWarnings("deprecation")
		Job job = new Job(conf, "mutualfriends");
		job.setJarByClass(MutualFriends.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);


		// set output key type
		job.setOutputKeyClass(Text.class);
		// set output value type
		job.setOutputValueClass(Text.class);
		//set the HDFS path of the input data
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		// set the HDFS path for the output
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		//Wait till job completion
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}