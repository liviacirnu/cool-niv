<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Message Board</title>
</head>
<body>


<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="container">
  <h2>
    Create a new message post
  </h2>

${confirmation}

  <form method="POST" action="/CreatePost">

    <div>
      <label for="title">Title</label>
      <input type="text" name="title" id="title" size="40" value="${fn:escapeXml(message.title)}" class="form-control" />
    </div>

    <div>
      <label for="author">Author</label>
      <input type="text" name="author" id="author" size="40" value="${fn:escapeXml(message.author)}" class="form-control" />
    </div>

    <div>
      <label for="description">Post content</label>
      <textarea name="description" id="description" rows="10" cols="50" class="form-control">${fn:escapeXml(blog.content)}</textarea>
    </div>

    <button type="submit">Save</button>
  </form>
</div>


</body>
</html>