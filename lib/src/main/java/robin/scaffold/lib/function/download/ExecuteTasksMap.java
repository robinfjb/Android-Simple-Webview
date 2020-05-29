package robin.scaffold.lib.function.download;

import java.util.concurrent.ConcurrentHashMap;


public class ExecuteTasksMap extends ConcurrentHashMap<ExecuteTasksMap.TaskData, Boolean> {

    public void removeTask(String url, String filePath) {
        super.remove(new TaskData(url, filePath));
    }

    public void addTask(String url, String filePath) {
        super.put(new TaskData(url, filePath), Boolean.TRUE);

    }

    public boolean contains(String url, String filePath) {
        return super.containsKey(new TaskData(url, filePath));
    }


    public static class TaskData {
        private String url;
        private String filePath;

        public TaskData(String url, String filePath) {
            this.url = url;
            this.filePath = filePath;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (!(obj instanceof TaskData)) {
                return false;
            }
            TaskData another = (TaskData) obj;
            return another.url.equals(url) && another.filePath.equals(filePath);
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + url.hashCode();
            result = 31 * result + filePath.hashCode();
            return result;
        }
    }
}
