package com.ryxc.mahout;


import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.util.List;

/**
 * Created by tonye0115 on 2016/11/8.
 */
public class MahoutHelloWorld {
    public static void main(String[] args) {
        Logger logger= Logger.getLogger(MahoutHelloWorld.class);
        try {
            //读取用户评分数据
            String path = MahoutHelloWorld.class.getResource("/").getPath();
            DataModel model = new FileDataModel(new File(path+"info.csv"));
            // 相似度 好朋友的标准
            UserSimilarity userSimilarity = new PearsonCorrelationSimilarity(model);
            // 邻域 选择两个好朋友帮我推荐
            UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2,userSimilarity, model);
            // 构建推荐引擎
            Recommender recommender = new GenericUserBasedRecommender(model,userNeighborhood, userSimilarity);
            // 进行推荐
            List<RecommendedItem> recommend = recommender.recommend(1, 5);
            for (RecommendedItem item : recommend) {
                logger.info(item);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


}
