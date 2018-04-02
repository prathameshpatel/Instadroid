package io.github.prathameshpatel.instadroid.model;

public class Data {

    private User user;
    private Images images;
    private Likes likes;
    private String created_time;
    private String id;
    private boolean user_has_liked;

    public User getUser() {
        return user;
    }

    public Images getImages() {
        return images;
    }

    public Likes getLikes() {
        return likes;
    }

    public String getCreated_time() {
        return created_time;
    }

    public String getId() {
        return id;
    }

    public boolean getUserHasLiked() {
        return user_has_liked;
    }

    public void setUserHasLiked(boolean hasLiked) {
        this.user_has_liked = hasLiked;
    }

    public class User {
        private String profile_picture;

        public String getProfile_picture() {
            return profile_picture;
        }
    }

    public class Images {
        private Standard_resolution standard_resolution;
        private Low_resolution low_resolution;
        private Thumbnail thumbnail;

        public Standard_resolution getStandard_resolution() {
            return standard_resolution;
        }

        public Low_resolution getLow_resolution() {
            return low_resolution;
        }

        public Thumbnail getThumbnail() {
            return thumbnail;
        }

        public class Standard_resolution {
            private String url;
            //height, width = 612
            public String getUrl() {
                return url;
            }
        }

        public class Low_resolution {
            private String url;
            //height, width = 306
            public String getUrl() {
                return url;
            }
        }

        public class Thumbnail {
            private String url;
            //height, width = 150
            public String getUrl() {
                return url;
            }
        }
    }

    public class Likes {
        private int count;

        public int getCount() {
            return count;
        }

        public void setCount(int newCount) {
            this.count = newCount;
        }
    }
}