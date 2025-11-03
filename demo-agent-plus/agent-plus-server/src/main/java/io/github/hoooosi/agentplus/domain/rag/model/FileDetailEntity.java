package io.github.hoooosi.agentplus.domain.rag.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.hoooosi.agentplus.domain.rag.constant.FileProcessingStatusEnum;
import io.github.hoooosi.agentplus.infrastructure.entity.BaseEntity;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;


@TableName("file_detail")
@Data
public class FileDetailEntity extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1055107743652307804L;

    /** 文件id */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /** 文件访问地址 */
    private String url;
    /** 文件大小，单位字节 */
    private Long size;
    /** 文件名称 */
    private String filename;
    /** 原始文件名 */
    private String originalFilename;
    /** 基础存储路径 */
    private String basePath;
    /** 存储路径 */
    private String path;
    /** 文件扩展名 */
    private String ext;
    /** MIME类型 */
    private String contentType;
    /** 存储平台 */
    private String platform;
    /** 缩略图访问路径 */
    private String thUrl;
    /** 缩略图名称 */
    private String thFilename;
    /** 缩略图大小，单位字节 */
    private Long thSize;
    /** 缩略图MIME类型 */
    private String thContentType;
    /** 文件所属对象id */
    private String objectId;
    /** 文件所属对象类型，例如用户头像，评价图片 */
    private String objectType;
    /** 文件元数据 */
    private String metadata;
    /** 文件用户元数据 */
    private String userMetadata;
    /** 缩略图元数据 */
    private String thMetadata;
    /** 缩略图用户元数据 */
    private String thUserMetadata;
    /** 附加属性 */
    private String attr;
    /** 文件ACL */
    private String fileAcl;
    /** 缩略图文件ACL */
    private String thFileAcl;
    /** 哈希信息 */
    private String hashInfo;
    /** 上传ID，仅在手动分片上传时使用 */
    private String uploadId;
    /** 上传状态，仅在手动分片上传时使用，1：初始化完成，2：上传完成 */
    private Integer uploadStatus;
    /** 用户ID */
    private String userId;
    /** 数据集id */
    private String dataSetId;
    /** 总页数 */
    private Integer filePageSize;
    /** 文件处理状态 */
    private Integer processingStatus;
    /** 当前OCR处理页数 */
    private Integer currentOcrPageNumber;
    /** 当前向量化处理页数 */
    private Integer currentEmbeddingPageNumber;
    /** OCR处理进度百分比 */
    private Double ocrProcessProgress;
    /** 向量化处理进度百分比 */
    private Double embeddingProcessProgress;
    @TableField(exist = false)
    private MultipartFile multipartFile;

    /** 兼容性方法：获取初始化状态（基于新的统一状态判断）
     * @return 初始化状态 */
    @Deprecated
    public Integer getIsInitialize() {
        if (processingStatus == null) {
            return 0; // 待初始化
        }
        FileProcessingStatusEnum status = FileProcessingStatusEnum.fromCode(processingStatus);
        return switch (status) {
            case UPLOADED -> 0; // 待初始化
            case OCR_PROCESSING -> 1; // 初始化中
            case OCR_COMPLETED, EMBEDDING_PROCESSING, EMBEDDING_FAILED, COMPLETED -> 2; // 已初始化
            case OCR_FAILED -> 3; // 初始化失败
            default -> 0;
        };
    }

    /** 兼容性方法：获取向量化状态（基于新的统一状态判断）
     * @return 向量化状态 */
    @Deprecated
    public Integer getIsEmbedding() {
        if (processingStatus == null) {
            return 0; // 未初始化
        }
        FileProcessingStatusEnum status = FileProcessingStatusEnum.fromCode(processingStatus);
        return switch (status) {
            case UPLOADED, OCR_PROCESSING, OCR_FAILED -> 0; // 未初始化
            case OCR_COMPLETED -> 0; // 待向量化（未初始化向量化）
            case EMBEDDING_PROCESSING -> 1; // 初始化中
            case COMPLETED -> 2; // 已初始化
            case EMBEDDING_FAILED -> 3; // 初始化失败
            default -> 0;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileDetailEntity that = (FileDetailEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(url, that.url) && Objects.equals(size, that.size)
                && Objects.equals(filename, that.filename) && Objects.equals(originalFilename, that.originalFilename)
                && Objects.equals(basePath, that.basePath) && Objects.equals(path, that.path)
                && Objects.equals(ext, that.ext) && Objects.equals(contentType, that.contentType)
                && Objects.equals(platform, that.platform) && Objects.equals(thUrl, that.thUrl)
                && Objects.equals(thFilename, that.thFilename) && Objects.equals(thSize, that.thSize)
                && Objects.equals(thContentType, that.thContentType) && Objects.equals(objectId, that.objectId)
                && Objects.equals(objectType, that.objectType) && Objects.equals(metadata, that.metadata)
                && Objects.equals(userMetadata, that.userMetadata) && Objects.equals(thMetadata, that.thMetadata)
                && Objects.equals(thUserMetadata, that.thUserMetadata) && Objects.equals(attr, that.attr)
                && Objects.equals(fileAcl, that.fileAcl) && Objects.equals(thFileAcl, that.thFileAcl)
                && Objects.equals(hashInfo, that.hashInfo) && Objects.equals(uploadId, that.uploadId)
                && Objects.equals(uploadStatus, that.uploadStatus) && Objects.equals(userId, that.userId)
                && Objects.equals(dataSetId, that.dataSetId) && Objects.equals(filePageSize, that.filePageSize)
                && Objects.equals(processingStatus, that.processingStatus)
                && Objects.equals(currentOcrPageNumber, that.currentOcrPageNumber)
                && Objects.equals(currentEmbeddingPageNumber, that.currentEmbeddingPageNumber)
                && Objects.equals(ocrProcessProgress, that.ocrProcessProgress)
                && Objects.equals(embeddingProcessProgress, that.embeddingProcessProgress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, size, filename, originalFilename, basePath, path, ext, contentType, platform,
                thUrl, thFilename, thSize, thContentType, objectId, objectType, metadata, userMetadata, thMetadata,
                thUserMetadata, attr, fileAcl, thFileAcl, hashInfo, uploadId, uploadStatus, userId, dataSetId,
                filePageSize, processingStatus, currentOcrPageNumber, currentEmbeddingPageNumber, ocrProcessProgress,
                embeddingProcessProgress);
    }
}
