package org.xllapp.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 此类用于处理批量的数据库操作,如批量插入,更新等.
 *
 * @author dylan.chen Nov 15, 2014
 * 
 */
public class BatchTemplate {
	
	private final static Logger logger = LoggerFactory.getLogger(BatchTemplate.class);
	
	public final static int DEFAULT_BATCH_SIZE = 100;
	
	public static <T> void execute(List<T> input , BatchCallback<T> callback){
		execute(input, DEFAULT_BATCH_SIZE, callback);
	}

	public static <T> void execute(List<T> input, int batchSize, BatchCallback<T> callback) {

		if (null == input || input.isEmpty()) {
			return;
		}

		int totalCount = input.size();
		List<T> list = new ArrayList<T>();
		int i = 0;
		for (T item : input) {
			list.add(item);
			if (list.size() == batchSize) {
				i++;
				doBatch(i,list,callback);
				list.clear();
				continue;
			}
			if ((i * batchSize + list.size()) == totalCount) {
				doBatch(i + 1, list, callback);
				break;
			}
		}
	}
	
	private static <T> void doBatch(int i, List<T> batch, BatchCallback<T> callback) {
		try {
			callback.doBatch(i, batch);
		} catch (Exception e) {
			logger.error("failure to do "+i+" batch[size:"+batch.size()+"].caused by:"+e.getLocalizedMessage(),e);
		}
	}

	public interface BatchCallback<T> {
		
		public void doBatch(int i, List<T> batch);
		
	}

}
