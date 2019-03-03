package com.liv;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Servlet implementation class CreatePost
 */
@WebServlet("/CreatePost")
public class CreatePost extends HttpServlet {
	DatastoreService datastore;

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			this.createPost(req, resp);
			this.showPosts();
		    req.getRequestDispatcher("/CreateMessagePost.jsp").forward(req, resp);
		} catch (ServletException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void init() throws ServletException {

	  // setup datastore service
	  datastore = DatastoreServiceFactory.getDatastoreService();
	}
	
	private void showPosts() {
		final Query q =
			    new Query("Post").setFilter(new FilterPredicate("title", FilterOperator.NOT_EQUAL, ""));

		PreparedQuery pq = datastore.prepare(q);
		List<Entity> posts = pq.asList(FetchOptions.Builder.withLimit(5)); // Retrieve up to five posts

		posts.forEach(
		    (result) -> {
		      // Grab the key and convert it into a string in preparation for encoding
		      String keyString = KeyFactory.keyToString(result.getKey());

		      // Encode the entity's key with Base64
		      String encodedID = new String(Base64.getUrlEncoder().encodeToString(String.valueOf(keyString).getBytes()));

		      // Build up string with values from the Datastore entity
		      String recordOutput =
		          String.format("%s: %s %s %s %s %s", result.getProperty("title"), result.getProperty("timestamp"),
		              result.getProperty("author"), encodedID, encodedID, result.getProperty("body"));

		      System.out.println(recordOutput); // Print out HTML
		    });
	}
	
	private void createPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
	  // Create a map of the httpParameters that we want and run it through jSoup
	  Map<String, String> postContent =
	      req.getParameterMap()
	          .entrySet()
	          .stream()
	          .filter(a -> a.getKey().startsWith("postContent_"))
	          .collect(
	              Collectors.toMap(
	                  p -> p.getKey(), p -> Jsoup.clean(p.getValue()[0], Whitelist.basic())));

	  Entity post = new Entity("Post"); // create a new entity

	  post.setProperty("title", postContent.get("PostContent_title"));
	  post.setProperty("author", postContent.get("PostContent_author"));
	  post.setProperty("body", postContent.get("PostContent_description"));
	  post.setProperty("timestamp", new Date().getTime());

	  try {
	    datastore.put(post); // store the entity

	    // Send the user to the confirmation page with personalised confirmation text
	    String confirmation = "Post with title " + postContent.get("postContent_title") + " created.";

	    req.setAttribute("confirmation", confirmation);
	  } catch (DatastoreFailureException e) {
	    throw new ServletException("Datastore error", e);
	  }
	}

}
