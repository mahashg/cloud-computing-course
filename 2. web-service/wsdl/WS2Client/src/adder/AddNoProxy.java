package adder;

public class AddNoProxy implements adder.AddNo {
  private String _endpoint = null;
  private adder.AddNo addNo = null;
  
  public AddNoProxy() {
    _initAddNoProxy();
  }
  
  public AddNoProxy(String endpoint) {
    _endpoint = endpoint;
    _initAddNoProxy();
  }
  
  private void _initAddNoProxy() {
    try {
      addNo = (new adder.AddNoServiceLocator()).getAddNo();
      if (addNo != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)addNo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)addNo)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (addNo != null)
      ((javax.xml.rpc.Stub)addNo)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public adder.AddNo getAddNo() {
    if (addNo == null)
      _initAddNoProxy();
    return addNo;
  }
  
  public int add(int x, int y) throws java.rmi.RemoteException{
    if (addNo == null)
      _initAddNoProxy();
    return addNo.add(x, y);
  }
  
  
}