package com.xnx3.spider.vo;

import com.xnx3.BaseVO;
import com.xnx3.spider.cache.Resource;

/**
 * 对应 {@link Resource}
 * @author 管雷鸣
 */
public class ResourceVO extends BaseVO{
	private Resource resource;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
}
