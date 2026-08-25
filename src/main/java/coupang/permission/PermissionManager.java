package coupang.permission;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Ryan Lee
 * @version $ PermissionManager, v 0.1 2026/8/24 17:36 Ryan Lee Exp $
 * @Description
 */
public class PermissionManager {

    public final static Map<String, User> USER_MAP = new ConcurrentHashMap<>();
    public final static Map<String, Role> ROLE_MAP = new ConcurrentHashMap<>();
    public final static Map<String, MyPermission> PERMISSION_MAP = new ConcurrentHashMap<>();

    static class User {
        private String userId;
        private String useName;
        private Set<String> roleList;

        public User(String useName) {
            this.useName = useName;
            this.roleList = new HashSet<>();
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUseName() {
            return useName;
        }

        public void setUseName(String useName) {
            this.useName = useName;
        }

        public Set<String> getRoleList() {
            return roleList;
        }

        public void setRoleList(Set<String> roleList) {
            this.roleList = roleList;
        }

        //注册
        public static User register(String userName) {
            User user = new User(userName);
            user.setUserId(UUID.randomUUID().toString());
            User user1 = USER_MAP.putIfAbsent(userName, user);
            if (null != user1) {
                throw new RuntimeException("用户已存在");
            }
            return user;
        }

        public static void bindRole(String userName, String role) {
            User user = USER_MAP.get(userName);
            if (null == user) {
                throw new RuntimeException("用户不存在");
            }
            Set<String> roleList1 = user.getRoleList();
            if (null == roleList1) {
                roleList1 = new HashSet<>();
            }
            roleList1.add(role);
        }

        //权限列表
        public static List<MyPermission> listPermissions(String userName) {
            User user = USER_MAP.get(userName);
            if (null == user) {
                throw new RuntimeException("用户不存在");
            }
            Set<String> userRoles = user.getRoleList();
            if (isEmpty(userRoles)) {
                return new ArrayList<>();
            }

            Set<String> permissions = new HashSet<>();
            for (String userRole : userRoles) {
                Role role = ROLE_MAP.get(userRole);
                if (null == role) {
                    continue;
                }
                if (null != role.getPermissionList()) {
                    permissions.addAll(role.getPermissionList());
                }
            }
            if (isEmpty(permissions)) {
                return new ArrayList<>();
            }

            return MyPermission.listByCodes(new ArrayList<>(permissions));
        }
    }

    private static boolean isEmpty(Set<String> set) {
        return null == set || set.isEmpty();
    }

    static class Role {

        private String roleId;
        private String roleCode;
        private String roleName;
        private Set<String> permissionList;

        public Role(String roleCode, String roleName) {
            this.roleCode = roleCode;
            this.roleName = roleName;
            this.permissionList = new HashSet<>();
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public String getRoleCode() {
            return roleCode;
        }

        public void setRoleCode(String roleCode) {
            this.roleCode = roleCode;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public Set<String> getPermissionList() {
            return permissionList;
        }

        public void setPermissionList(Set<String> permissionList) {
            this.permissionList = permissionList;
        }

        public static Role create(String roleCode, String roleName) {
            Role role = new Role(roleCode, roleName);
            role.setRoleId(UUID.randomUUID().toString());
            Role oldRole = ROLE_MAP.putIfAbsent(roleCode, role);
            if (null != oldRole) {
                throw new RuntimeException("roleCode已存在");
            }
            return role;
        }

        public static void bindPermission(String roleCode, List<String> permissionList) {
            Role role = ROLE_MAP.get(roleCode);
            if (null == role) {
                throw new RuntimeException("角色不存在");
            }

            Set<String> permissionList1 = role.getPermissionList();
            if (null == permissionList1) {
                permissionList1 = new HashSet<>();
            }
            permissionList1.addAll(permissionList);
        }


    }

    static class MyPermission {
        private String permissionId;
        private String permissionCode;
        private String permissionName;

        public MyPermission(String permissionCode, String permissionName) {
            this.permissionCode = permissionCode;
            this.permissionName = permissionName;
        }

        public String getPermissionId() {
            return permissionId;
        }

        public void setPermissionId(String permissionId) {
            this.permissionId = permissionId;
        }

        public String getPermissionCode() {
            return permissionCode;
        }

        public void setPermissionCode(String permissionCode) {
            this.permissionCode = permissionCode;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public static MyPermission create(String permissionCode, String permissionName) {
            MyPermission myPermission = new MyPermission(permissionCode, permissionName);
            myPermission.setPermissionId(UUID.randomUUID().toString());
            MyPermission oldPermission = PERMISSION_MAP.putIfAbsent(permissionCode, myPermission);
            if (null != oldPermission) {
                throw new RuntimeException("当前权限code已存在");
            }
            return myPermission;
        }

        public static List<MyPermission> listByCodes(List<String> permissionCodes) {
            return permissionCodes.stream().map(PERMISSION_MAP::get).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    public static void main(String[] args) {

        MyPermission.create("p1", "权限1");
        MyPermission.create("p2", "权限2");
        MyPermission.create("p3", "权限3");

        Role role1 = Role.create("role1", "角色1");
        Role role2 = Role.create("role2", "角色2");
        Role role3 = Role.create("role3", "角色3");

        Role.bindPermission("role1", Arrays.asList("p1","p3"));
        Role.bindPermission("role2", Arrays.asList("p2"));
        Role.bindPermission("role3", Arrays.asList("p3"));


        User userZ = User.register("张三");
        User userL = User.register("李四");
        User.bindRole("张三", "role1");
        User.bindRole("张三", "role3");
        User.bindRole("李四", "role2");

        List<MyPermission> myPermissions = User.listPermissions("张三");
        System.out.println("张三: " + myPermissions.stream().map(MyPermission::getPermissionName).collect(Collectors.joining(",")));
        System.out.println("李四: " + User.listPermissions("李四").stream().map(MyPermission::getPermissionName).collect(Collectors.joining(",")));

        System.out.println("张三加角色================");

        User.bindRole("张三", "role2");
        System.out.println("张三: " + User.listPermissions("张三").stream().map(MyPermission::getPermissionName).collect(Collectors.joining(",")));
        System.out.println("李四: " + User.listPermissions("李四").stream().map(MyPermission::getPermissionName).collect(Collectors.joining(",")));


    }


}
