package com.customer.api.service;

import java.util.List;

import com.customer.api.entity.Region;

public interface SvcRegion {

	List<Region> getRegions() throws Exception;
	Region getRegion(Integer region_id);
	String createRegion(Region region);
	String updateRegion(Integer region_id, Region region);
	String deleteRegion(Integer region_id);
}
