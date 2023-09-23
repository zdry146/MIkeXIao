package com.mongodb.tradecraft.social_server;

import static spark.Spark.after;
import static spark.Spark.put;
import static spark.Spark.post;
import static spark.Spark.get;
import static spark.Spark.delete;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.logging.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class SocialService {
	static final String version = "0.0.1";
	static Logger logger;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();

		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();

		logger = LoggerFactory.getLogger(SocialService.class);
		logger.info(version);
		
	    String URI="mongodb://mike-virtual-machine1:27017,mike-virtual-machine2:27017,mike-virtual-machine3:27017/?replicaSet=rs0&retryWrites=true&w=majority&wtimeoutMS=3000&readConcernLevel=majority";

        if(args.length > 0)
        {
                URI = args[0];
                
        }
        MongoClient mongoClient = new MongoClient(new MongoClientURI(URI));
		APIRoutes userRoutes = new APIRoutes(mongoClient);
		
			
					
			//Create a new user
			post("/users",(req,res) -> userRoutes.createUser(req,res));
			
			//Fetch some basic info about a user (cannonical username, date joined)
			get("/users/*/profile",(req,res) -> userRoutes.getUserProfile(req,res));

			//Add a new follower to a User
			put("/users/*/followers/*",(req,res) -> userRoutes.followUser(req,res));
			//Remove a follower from a User
			delete("/users/*/followers/*",(req,res) -> userRoutes.unFollowUser(req,res));
			//Get a list of followers for a user
			get("/users/*/followers",(req,res) -> userRoutes.getFollowers(req,res));
			//Get a count of followers for a user (may be cached)
			get("/users/*/followers_count",(req,res) -> userRoutes.getFollowersCount(req,res));

			//Get a list of those a user follows
			get("/users/*/following",(req,res) -> userRoutes.getFollowing(req,res));
			//Get  a   count of how many a user follows
			get("/users/*/following_count",(req,res) -> userRoutes.getFollowingCount(req,res));
		
	
			//Get the latest page of a users feed
			get("/users/*/feed",(req,res) ->  userRoutes.getFeed(req,res));
			

			//Post a new item to followers
			post("/users/*/posts",(req,res) -> userRoutes.newPost(req,res));
			//Get all posts by a user
			get("/users/*/posts",(req,res) ->  userRoutes.getPosts(req,res));
			//Get all posts count by a user
			get("/users/*/posts_count",(req,res) ->  userRoutes.getPostsCount(req,res));
			//Delete a post previously sent
			delete("/users/*/posts/*",(req,res) -> userRoutes.deletePost(req,res)); //Task1

			put("/users/*/nickname/*",(req,res) -> userRoutes.changeNickname(req,res));

			after((req, res) -> {
				res.type("application/json");
			});

			//Uncomment to run background workers
			StartWorkers(mongoClient);
		

		return;
	}

	private static void StartWorkers(MongoClient mongoClient)
	{
		int nThreads = 1; //How many workers
		ExecutorService simexec = Executors.newFixedThreadPool(nThreads);
		for (int workerno = 0; workerno < nThreads; workerno++) {
			simexec.execute(new WorkerTask(workerno,mongoClient));
		}
		simexec.shutdown();
	}

}
