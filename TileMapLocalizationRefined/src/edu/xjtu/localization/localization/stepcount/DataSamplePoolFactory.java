package edu.xjtu.localization.localization.stepcount;

import org.apache.commons.pool.PoolableObjectFactory;

public class DataSamplePoolFactory implements PoolableObjectFactory<DataSample> {
	@Override
	public void activateObject(DataSample arg0) throws Exception {
		arg0.setValid(true);
	}

	@Override
	public void destroyObject(DataSample arg0) throws Exception {
		arg0.setValid(false);
		arg0 = null;
	}

	@Override
	public void passivateObject(DataSample arg0) throws Exception {
		arg0.setValid(false);
	}

	@Override
	public boolean validateObject(DataSample arg0) {
		// TODO Auto-generated method stub
		return arg0.isValid();
	}

	@Override
	public DataSample makeObject() throws Exception {
		return new DataSample();
	}

}
