package org.wordpress.android.fluxc.model;

import com.yarolegovich.wellsql.core.Identifiable;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.Table;

import org.wordpress.android.fluxc.utils.MediaUtils;
import org.wordpress.android.util.StringUtils;

import java.io.Serializable;

@Table
public class MediaModel implements Identifiable, Serializable {
    public enum UploadState {
        QUEUED("queued"),
        UPLOADING("uploading"),
        DELETE("delete"),
        DELETED("deleted"),
        FAILED("failed"),
        UPLOADED("uploaded");

        private String mDescriptor;
        UploadState(String descriptor) {
            mDescriptor = descriptor;
        }

        @Override
        public String toString() {
            return mDescriptor;
        }
    }

    @PrimaryKey
    @Column private int mId;

    // Associated IDs
    @Column private long mSiteId;
    @Column private long mMediaId;
    @Column private long mPostId;
    @Column private long mAuthorId;
    @Column private String mGuid;

    // Remote creation date
    @Column private String mUploadDate;

    // Remote Url's
    @Column private String mUrl;
    @Column private String mThumbnailUrl;

    // File descriptors
    @Column private String mFileName;
    @Column private String mFilePath;
    @Column private String mFileExtension;
    @Column private String mMimeType;

    // Descriptive strings
    @Column private String mTitle;
    @Column private String mCaption;
    @Column private String mDescription;
    @Column private String mAlt;

    // Image and Video files only
    @Column private int mWidth;
    @Column private int mHeight;

    // Video and Audio files only
    @Column private int mLength;

    // Video only
    @Column private String mVideoPressGuid;
    @Column private boolean mVideoPressProcessingDone;

    // Local only
    @Column private String mUploadState;

    //
    // Legacy
    //
    @Column private int mHorizontalAlignment;
    @Column private boolean mVerticalAlignment;
    @Column private boolean mFeatured;
    @Column private boolean mFeaturedInPost;

    // Set to true on a successful response to delete via WP.com REST API, not stored locally
    private boolean mDeleted;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof MediaModel)) return false;

        MediaModel otherMedia = (MediaModel) other;

        return getId() == otherMedia.getId() && getSiteId() == otherMedia.getSiteId()
                && getMediaId() == otherMedia.getMediaId() && getPostId() == otherMedia.getPostId()
                && getAuthorId() == otherMedia.getAuthorId() && getWidth() == otherMedia.getWidth()
                && getHeight() == otherMedia.getHeight() && getLength() == otherMedia.getLength()
                && getHorizontalAlignment() == otherMedia.getHorizontalAlignment()
                && getVerticalAlignment() == otherMedia.getVerticalAlignment()
                && getVideoPressProcessingDone() == otherMedia.getVideoPressProcessingDone()
                && getFeatured() == otherMedia.getFeatured()
                && getFeaturedInPost() == otherMedia.getFeaturedInPost()
                && StringUtils.equals(getGuid(), otherMedia.getGuid())
                && StringUtils.equals(getUploadDate(), otherMedia.getUploadDate())
                && StringUtils.equals(getUrl(), otherMedia.getUrl())
                && StringUtils.equals(getThumbnailUrl(), otherMedia.getThumbnailUrl())
                && StringUtils.equals(getFileName(), otherMedia.getFileName())
                && StringUtils.equals(getFilePath(), otherMedia.getFilePath())
                && StringUtils.equals(getFileExtension(), otherMedia.getFileExtension())
                && StringUtils.equals(getMimeType(), otherMedia.getMimeType())
                && StringUtils.equals(getTitle(), otherMedia.getTitle())
                && StringUtils.equals(getDescription(), otherMedia.getDescription())
                && StringUtils.equals(getCaption(), otherMedia.getCaption())
                && StringUtils.equals(getAlt(), otherMedia.getAlt())
                && StringUtils.equals(getVideoPressGuid(), otherMedia.getVideoPressGuid())
                && StringUtils.equals(getUploadState(), otherMedia.getUploadState());
    }

    @Override
    public void setId(int id) {
        mId = id;
    }

    @Override
    public int getId() {
        return mId;
    }

    public void setSiteId(long siteId) {
        mSiteId = siteId;
    }

    public long getSiteId() {
        return mSiteId;
    }

    public void setMediaId(long mediaId) {
        mMediaId = mediaId;
    }

    public long getMediaId() {
        return mMediaId;
    }

    public void setPostId(long postId) {
        mPostId = postId;
    }

    public long getPostId() {
        return mPostId;
    }

    public void setAuthorId(long authorId) {
        mAuthorId = authorId;
    }

    public long getAuthorId() {
        return mAuthorId;
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public String getGuid() {
        return mGuid;
    }

    public void setUploadDate(String uploadDate) {
        mUploadDate = uploadDate;
    }

    public String getUploadDate() {
        return mUploadDate;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFileExtension(String fileExtension) {
        mFileExtension = fileExtension;
    }

    public String getFileExtension() {
        return mFileExtension;
    }

    public void setMimeType(String mimeType) {
        mMimeType = mimeType;
    }

    public String getMimeType() {
        return mMimeType;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setAlt(String alt) {
        mAlt = alt;
    }

    public String getAlt() {
        return mAlt;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setLength(int length) {
        mLength = length;
    }

    public int getLength() {
        return mLength;
    }

    public void setVideoPressGuid(String videoPressGuid) {
        mVideoPressGuid = videoPressGuid;
    }

    public String getVideoPressGuid() {
        return mVideoPressGuid;
    }

    public void setVideoPressProcessingDone(boolean videoPressProcessingDone) {
        mVideoPressProcessingDone = videoPressProcessingDone;
    }

    public boolean getVideoPressProcessingDone() {
        return mVideoPressProcessingDone;
    }

    public void setUploadState(String uploadState) {
        mUploadState = uploadState;
    }

    public String getUploadState() {
        return mUploadState;
    }

    //
    // Legacy methods
    //

    public boolean isVideo() {
        return MediaUtils.isVideoMimeType(getMimeType());
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        mHorizontalAlignment = horizontalAlignment;
    }

    public int getHorizontalAlignment() {
        return mHorizontalAlignment;
    }

    public void setVerticalAlignment(boolean verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    public boolean getVerticalAlignment() {
        return mVerticalAlignment;
    }

    public void setFeatured(boolean featured) {
        mFeatured = featured;
    }

    public boolean getFeatured() {
        return mFeatured;
    }

    public void setFeaturedInPost(boolean featuredInPost) {
        mFeaturedInPost = featuredInPost;
    }

    public boolean getFeaturedInPost() {
        return mFeaturedInPost;
    }

    public void setDeleted(boolean deleted) {
        mDeleted = deleted;
    }

    public boolean getDeleted() {
        return mDeleted;
    }
}