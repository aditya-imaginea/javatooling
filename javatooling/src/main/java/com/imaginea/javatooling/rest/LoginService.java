package com.imaginea.javatooling.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/login")
public class LoginService {

	@GET
	@Path("validate")
	public Response validate(@QueryParam("name") String name,
			@QueryParam("passwd") String passwd) {

		String result = "Restful example : " + name + " " + passwd;

		return Response.status(200).entity(result).build();

	}
}
