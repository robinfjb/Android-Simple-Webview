package robin.scaffold.lib.function.js;

public class JsCallJava {
    private final static String TAG = "JsCallJava";
    private Object mInterfaceObj;
    private String mInterfacedName;

    public JsCallJava(Object interfaceObj, String interfaceName) {
        mInterfaceObj = interfaceObj;
        mInterfacedName = interfaceName;
    }
    public Object getmInterfaceObj() {
        return mInterfaceObj;
    }

    public void setmInterfaceObj(Object mInterfaceObj) {
        this.mInterfaceObj = mInterfaceObj;
    }

    public String getmInterfacedName() {
        return mInterfacedName;
    }

    public void setmInterfacedName(String mInterfacedName) {
        this.mInterfacedName = mInterfacedName;
    }
}
