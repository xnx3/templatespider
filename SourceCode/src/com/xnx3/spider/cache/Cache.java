package com.xnx3.spider.cache;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.xnx3.BaseVO;
import com.xnx3.DateUtil;
import com.xnx3.file.FileUtil;
import com.xnx3.spider.Global;
import com.xnx3.spider.vo.ResourceVO;

/**
 * 缓存队列，缓存相关操作
 * @author 管雷鸣
 */
public class Cache {
	/**
	 * 当前缓存的资源。key：resourceBean.netUrl
	 * <br/>可以通过传入资源的绝对路径，来判断此资源是否缓存过。无论是待缓存，还是已缓存完毕，都会在这里有
	 * <br/>同时，在项目打开时，也会从本地读取已缓存的资源列表，将其缓存到此map
	 */
	public static Map<String, Resource> cacheMap = new HashMap<String, Resource>();
	
	/**
	 * 将资源加入缓存序列。
	 * @param rb 要加入的资源
	 * @return {@link ResourceBean}
	 * 			<ul>
	 * 				<li>若加入成功。等待进行缓存，则此处会返回null</li>
	 * 				<li>若加入失败，因为之前已经缓存过了。也就是已经有当前的缓存文件了。那么将缓存文件中的 {@link ResourceBean}返回 </li>
	 * 			</ul>
	 */
	public static synchronized ResourceVO addCache(Resource resource){
		ResourceVO vo = new ResourceVO();
		if(resource.getNetUrl() == null){
			//无远程资源，退出不缓存
			vo.setResult(BaseVO.FAILURE);
			return vo;
		}
		if(resource.getNetUrl().length() > 255){
			//远程资源可能不是一个正常的url，可能是图片的base64存在，退出不缓存。因为本身就跟随其父文件进行了缓存了
			vo.setResult(BaseVO.FAILURE);
			return vo;
		}
		
		//找cacheMap中是否缓存了
		Resource cacheResource = cacheMap.get(resource.getNetUrl());
		if(cacheResource == null){
			cacheResource = cacheMap.get(resource.getOriginalUrl());
			if(cacheResource != null){
				System.out.println("cacheResource-->"+cacheResource);
			}
		}
		
		//如果缓存中有，那么要将之前的缓存返回
		if(cacheResource != null){
			//已经缓存过了，不用再进行缓存
			vo.setResource(cacheResource);
		}else{
			//如果缓存中没有，那么进行缓存，将其下载下来
			downFile(resource);
			//将绝对路径其加入缓存
			cacheMap.put(resource.getNetUrl(), resource);
			//将其原始引用路径加入缓存
			cacheMap.put(resource.getOriginalUrl(), resource);
			
			vo.setResource(resource);
		}
		
		return vo;
	}
	
	/**
	 * 将 {@link #cacheMap} 缓存到本地磁盘，文件下载
	 */
	public static synchronized void downFile(Resource resource){
		try {
			Thread.sleep(Global.delayTime);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			FileUtil.downFiles(resource.getNetUrl(), resource.getLocalUrl());
		}catch(java.io.FileNotFoundException notFind){
			Global.log("404 File Not Found !  "+resource.getNetUrl());
		}catch(java.lang.NullPointerException nullE){
			Global.log("downFile NULL----- "+resource.getNetUrl());
		}catch (Exception e) {
			Global.log(e.getMessage()+" --- "+resource.getNetUrl());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		//https://subject.autohome.com.cn/dealer/2021/3/gacmotor_sd/
		FileUtil.downFiles("http://", "/images/1.html");
	}
}
