package coupang;


import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Ryan Lee
 * @version $ test, v 0.1 2025/8/6 15:04 Ryan Lee Exp $
 * @Description 实现一个类，基于内存实现文件和目录的管理和操作
 * 1、可以通过类实现文件和目录的创建、删除、移动
 * 2、能列出目录下的文件和子目录
 * 2、实现ls命令
 * 3、跑通基本case
 * <p>
 * 路径解析：处理绝对路径
 * 创建目录接口
 * 创建文件接口
 * 列出目录内容
 * 删除文件或者空目录
 * 目录/文件的移动
 */
public class FileSystem {

    public static final FileNode root = new FileNode("");

    static class FileNode {
        private String fileName;
        private FileNode parent;
        private Map<String, FileNode> children;

        /**
         * 文件类型 0:文件夹 1:文件
         */
        private int type;



        public FileNode(String fileName) {
            this.fileName = fileName;
            this.children = new HashMap<>();
            this.type = 0;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public FileNode getParent() {
            return parent;
        }

        public void setParent(FileNode parent) {
            this.parent = parent;
        }

        public Map<String, FileNode> getChildren() {
            return children;
        }

        public void setChildren(Map<String, FileNode> children) {
            this.children = children;
        }

        public Collection<FileNode> ls(String path) {
            if (null == path || path.isEmpty()) {
                return this.getChildren().values();
            }
            FileNode fileNode = cd(resolvePath(path));
            if (null == fileNode) {
                System.out.println("目录不存在");
                return null;
            }
            Set<String> strings = fileNode.getChildren().keySet();
            System.out.println(String.join(" ", strings));
            return fileNode.getChildren().values();
        }

        public void rm(String path) {
            if (null == path || path.isEmpty()) {
                throw new RuntimeException("无指定的路径");
            }
            FileNode cd = cd(path);
            if (null == cd) {
                return;
            }

            if (cd.getType() == 1) {
                cd.parent.getChildren().remove(cd.getFileName());
                return;
            }

            if (!cd.getChildren().isEmpty()) {
                throw new RuntimeException("文件夹不为空，不能删除");
            }

            cd.parent.getChildren().remove(cd.getFileName());
        }

        public void move(String sourcePath, String targetSource) {
            FileNode cd = cd(sourcePath);
            if (null == cd) {
                return;
            }

            FileNode mkdir = mkdir(targetSource);
            cd.setParent(mkdir);
            mkdir.getChildren().put(cd.getFileName(), cd);
        }

        private FileNode cd(String path) {
            return cd(resolvePath(path));
        }


        private FileNode cd(String[] names) {
            FileNode tmp = this;
            for (String name : names) {
                if (null == name || name.equals("")) {
                    continue;
                }
                tmp = tmp.getChild(name);
                if (null == tmp) {
                    return null;
                }
            }
            return tmp;
        }


        public FileNode creatFile(String pathName) {
            FileNode mkdir = mkdir(pathName);
            if (null == mkdir) {
                return null;
            }
            mkdir.setType(1);
            return mkdir;
        }

        public FileNode mkdir(String pathName) {
            if (null == pathName || pathName.isEmpty()) {
                return null;
            }

            return mkdir(resolvePath(pathName));
        }


        private FileNode mkdir(String... names) {
            if (null == names || names.length == 0) {
                return null;
            }
            FileNode fileNode = this;
            for (String name : names) {
                if (fileNode.getType() == 1) {
                    throw new RuntimeException("当前路径存在同名的文件");
                }

                if (null == name || name.isEmpty()) {
                    continue;
                }
                fileNode = fileNode.addChild(name);
            }
            return fileNode;
        }

        private FileNode addChild(String name) {
            if (null == name || name.isEmpty()) {
                return null;
            }
            FileNode fileNode = this.getChild(name);
            if (null != fileNode) {
                return fileNode;
            }
            fileNode = new FileNode(name);
            fileNode.setParent(this);
            this.getChildren().put(name, fileNode);
            return fileNode;
        }

        private FileNode getChild(String fileName) {
            return this.getChildren().get(fileName);
        }

        private String[] resolvePath(String path) {
            if (null == path || path.isEmpty()) {
                return null;
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path.split("/");
        }

    }

    public static void main(String[] args) {
        //创建文件夹
        root.mkdir("/user/lhm/dir1");
        root.mkdir("/user1/lhm/dir1");
        root.mkdir("/user/lhm/dir2");
        root.mkdir("/user/lhm/dir1/dir11");

        //创建文件
        root.creatFile("/user/lhm/dir1/file1");

        //列出文件
        Collection<FileNode> ls = root.ls("/user/lhm/dir1");




        root.ls("/user/lhm/dir1");
        //移动
        root.move("/user/lhm/dir1/file1", "/user/lhm/dir2");
        root.ls("/user/lhm/dir2");

        root.rm("/user/lhm/dir2/file1");
        root.ls("/user/lhm/dir2");
        root.ls("/user/lhm");
        root.ls("/");


    }


}
