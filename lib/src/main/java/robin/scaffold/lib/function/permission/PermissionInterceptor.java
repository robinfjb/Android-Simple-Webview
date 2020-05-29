package robin.scaffold.lib.function.permission;


public interface PermissionInterceptor {
    /**
     *
     * @param url
     * @param permissions
     * @param requestCode
     * @param listener
     * @return true拦截事件，自定义权限是否开启（仅对位置有用，其他不予工作）,false则不予干扰
     */
    boolean intercept(String url, String[] permissions, int requestCode, PermissionCallbackListener listener);
}
