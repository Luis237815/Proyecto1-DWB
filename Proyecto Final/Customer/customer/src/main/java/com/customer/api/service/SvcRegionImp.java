package com.customer.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.customer.api.entity.Region;
import com.customer.api.repository.RepoRegion;

@Service
public class SvcRegionImp implements SvcRegion {

	@Autowired
	RepoRegion repo;
	
	@Override
	public List<Region> getRegions() {
		return repo.findByStatus(1);
	}

	@Override
	public Region getRegion(Integer region_id) {
		return repo.findByRegionId(region_id);
	}

	@Override
	public String createRegion(Region region) {
		Region regionSaved = (Region) repo.findByRegion(region.getRegion());
		if(regionSaved != null) { 
			if(regionSaved.getStatus() == 0) {
				repo.activateRegion(regionSaved.getRegion_id());
				return "region has been activated";
			}
			else
				return "region alredy exists";
		}
		repo.createRegion(region.getRegion());
		return "region created";
	}

	@Override
	public String updateRegion(Integer region_id, Region region) {
		Region regionSaved = (Region) repo.findByRegionId(region_id);
		if(regionSaved == null)
			return "region does not exist";
		else {
			if(regionSaved.getStatus() == 0)
				return "region is not active";
			else {
				regionSaved = (Region) repo.findByRegion(region.getRegion());
				if(regionSaved != null)
					return "region alredy exists";
				repo.updateRegion(region_id, region.getRegion());
				return "region updated";
			}
		}
	}

	@Override
	public String deleteRegion(Integer region_id) {
		Region regionSaved = (Region) repo.findByRegionId(region_id);
		if(regionSaved == null)
			return "region does not exist";
		else {
			repo.deleteById(region_id);
			return "region removed";
		}
	}

}
