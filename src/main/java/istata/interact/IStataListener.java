package istata.interact;

import istata.interact.model.StataResult;

public interface IStataListener {

	public void handleUpdate(String update);
	
	public void handleResult(StataResult result);
}
