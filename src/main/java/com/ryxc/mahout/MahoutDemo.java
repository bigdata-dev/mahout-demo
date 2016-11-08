package com.ryxc.mahout;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class MahoutDemo {

	//组装datamodel
	
	// userid itemid score
	// 101 102 103 104
	// 1(5,4,2,)
	// 2(,2,4,1)
	// 3(4,3,1,)
	DataModel dataModel;
	
	@Before
	public void initData() throws Exception{
		//每一个用户的喜好列表 key：用户id  value：该用户的偏好列表
		FastByIDMap<PreferenceArray> data=new FastByIDMap<PreferenceArray>();
		//组装第一个用户 偏好列表
		PreferenceArray array1=new GenericUserPreferenceArray(3);
		//PreferenceArray index 指：偏好列表的index 序号。
		array1.setUserID(0, 1);
		array1.setItemID(0, 101);
		array1.setValue(0, 5);
		
		array1.setUserID(1, 1);
		array1.setItemID(1, 102);
		array1.setValue(1, 4);
		
		array1.setUserID(2, 1);
		array1.setItemID(2, 103);
		array1.setValue(2, 2);
		
		data.put(1, array1);
		
		//组装第二个喜好
		PreferenceArray array2=new GenericUserPreferenceArray(3);
		//2(,2,4,1)
		array2.set(0, new GenericPreference(2,102,2));
		array2.set(1, new GenericPreference(2,103,4));
		array2.set(2, new GenericPreference(2,104,1));
		data.put(2, array2);
		//组装第三个喜好
		PreferenceArray array3=new GenericUserPreferenceArray(3);
		//3(4,3,1,)
		array3.set(0, new GenericPreference(3,101,4));
		array3.set(1, new GenericPreference(3,102,3));
		array3.set(2, new GenericPreference(3,103,1));
		data.put(3, array3);
		
		//dataModel=new GenericDataModel(data);
		
		//1  101 102 103
		//2 102 103 
		// key为userid value:物品的集合 set
		FastByIDMap<FastIDSet> userData=new FastByIDMap<FastIDSet>();
		
		FastIDSet userSet1=new FastIDSet(3);
		userSet1.add(101);
		userSet1.add(102);
		userSet1.add(103);
		userData.put(1,userSet1);
		
		FastIDSet userSet2=new FastIDSet(2);
		userSet2.add(102);
		userSet2.add(103);
		userData.put(2,userSet2);
		
		
		//无偏好的构建
//		dataModel=new GenericBooleanPrefDataModel(userData);

		String path = MahoutHelloWorld.class.getResource("/").getPath();
		//读取文件 有偏好的
		//dataModel=new FileDataModel(new File(path+"info.csv"));
		//读取文件 无偏好的
		dataModel=new FileDataModel(new File(path+"ubool.data"));
		
		
//		对于无偏好数据：getvalue：如果存在记录则是1.0；否则为null。  
//		System.out.println(dataModel.getPreferenceValue(1, 104));
//		System.out.println(dataModel.getItemIDsFromUser(1));
////		System.out.println(dataModel.getUserIDs());
		
	}
	@Ignore
	public void testUserSimi() throws Exception{
		
		//利用model和相似度函数 计算用户相似度
//		UserSimilarity userSimilarity=new TanimotoCoefficientSimilarity(dataModel);
		UserSimilarity userSimilarity=new PearsonCorrelationSimilarity(dataModel);
		userSimilarity=new CachingUserSimilarity(userSimilarity, dataModel);
		//查询用户之间的相似度  0.9999999999999998	0.944911182523068
		//如果使用CachingUserSimilarity userSimilarity(1,5) 第二次不会再次计算了
		System.out.println(userSimilarity.userSimilarity(1, 5));
		System.out.println(userSimilarity.userSimilarity(1, 5));
	}
	@Ignore
	public void testItemSimi() throws Exception{
		
		//利用model和相似度函数 计算物品相似度
		ItemSimilarity itemSimilarity=new PearsonCorrelationSimilarity(dataModel);
		itemSimilarity =new CachingItemSimilarity(itemSimilarity,dataModel);
		//查询物品之间的相似度 0.9449111825230729
		System.out.println(itemSimilarity.itemSimilarity(101, 102));
	}
	@Ignore
	public void testuserNei() throws Exception{
		//相似度
		UserSimilarity userSimilarity=new PearsonCorrelationSimilarity(dataModel);
		//固定数目的邻居  如果取邻居 只取前三个 
//		UserNeighborhood userNeighborhood=new NearestNUserNeighborhood(3,userSimilarity,dataModel);
//		long[] userNeighborhoods = userNeighborhood.getUserNeighborhood(1);
//		for (long l : userNeighborhoods) {
//			System.out.println(l+"NearestNUserNeighborhoodsimi---"+userSimilarity.userSimilarity(1, l));
//		}
//		4simi---0.9999999999999998
//		5simi---0.944911182523068
//		2simi----0.7642652566278799

		
		//固定阈值的邻居
		UserNeighborhood userNeighborhood=new ThresholdUserNeighborhood(-0.8,userSimilarity,dataModel);
		long[] userNeighborhoodsnew = userNeighborhood.getUserNeighborhood(1);
//		System.out.println(userSimilarity.userSimilarity(1, 2));
		for (long l : userNeighborhoodsnew) {
			System.out.println(l+"ThresholdUserNeighborhoodsimi---"+userSimilarity.userSimilarity(1, l));
		}
	}
	@Ignore
	public void testItemCmd() throws Exception{
		//基于物品的有偏好的推荐
//		ItemSimilarity itemSimilarity=new PearsonCorrelationSimilarity(dataModel);
//		Recommender recommender=new GenericItemBasedRecommender(dataModel,itemSimilarity);
		
		//无偏好推荐
		ItemSimilarity itemSimilarity=new TanimotoCoefficientSimilarity(dataModel);
		Recommender recommender=new GenericBooleanPrefItemBasedRecommender(dataModel,itemSimilarity);
		
		
		List<RecommendedItem> recommend = recommender.recommend(1, 3);
		for (RecommendedItem recommendedItem : recommend) {
			System.out.println(recommendedItem);
		}
	}
	@Test
	public void testUserCmd() throws Exception{
		//基于用户的有偏好的推荐
		//UserSimilarity userSimilarity=new PearsonCorrelationSimilarity(dataModel);
		//基于用户的无偏好的推荐
		UserSimilarity userSimilarity=new TanimotoCoefficientSimilarity(dataModel);
		
		UserNeighborhood userNeighborhood=new NearestNUserNeighborhood(3,userSimilarity,dataModel);
		Recommender recommender=new GenericUserBasedRecommender(dataModel,userNeighborhood,userSimilarity);
		List<RecommendedItem> recommend = recommender.recommend(1, 3);
		for (RecommendedItem recommendedItem : recommend) {
			System.out.println(recommendedItem);
		}
	}
	
	
}
