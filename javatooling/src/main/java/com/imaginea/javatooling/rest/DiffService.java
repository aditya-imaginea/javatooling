package com.imaginea.javatooling.rest;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;

import com.imaginea.javatooling.entity.CompareResponse;

@Path("/diffservice")
public class DiffService {

	@GET
	@Path("output")
	@Produces("application/json")
	@BadgerFish
	public CompareResponse validate(@QueryParam("added") String added,
			@QueryParam("deleted") String deleted) {

		// String result = "Restful example : " + name + " " + passwd;
		List<String> adds = new ArrayList<String>();
		adds.add(added);
		List<String> deletes = new ArrayList<String>();
		deletes.add(deleted);

		CompareResponse diff = new CompareResponse(adds, deletes);
		return diff;

	}
}
