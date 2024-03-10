package com.shoppingmall.utils;

import com.shoppingmall.common.response.ErrorCode;
import com.shoppingmall.constant.FileExtension;
import com.shoppingmall.constant.DirPathType;
import com.shoppingmall.dto.request.FileSaveRequestDto;
import com.shoppingmall.dto.request.PostFileSaveRequestDto;
import com.shoppingmall.dto.request.ProductFileSaveRequestDto;
import com.shoppingmall.dto.response.FileResponseDto;
import com.shoppingmall.exception.FailDownloadFilesException;
import com.shoppingmall.exception.FailSaveFileException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Getter
@Component
public class FileHandlerHelper {

    @Value("${file.upload-dir}")
    private String uploadPath;

    private final ResourceLoader resourceLoader;

    public FileHandlerHelper(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Linux Server 특정 경로(외부 경로)에 도메인에 따라서(POSTS, PRODUCT) 파일 업로드 진행
     * @param multipartFiles 업로드 하고자 하는 파일 목록 (리스트)
     * @param dirPathType 파일 경로 지정 시 사용되는 이름 ()
     * @return
     */
    public List<FileSaveRequestDto> uploadFiles(List<MultipartFile> multipartFiles, DirPathType dirPathType) {
        List<FileSaveRequestDto> baseFileSaveRequestDtos = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (file.isEmpty()) {
                continue;
            }
            FileSaveRequestDto baseFileSaveRequestDto = transferTo(file, dirPathType);
            baseFileSaveRequestDtos.add(baseFileSaveRequestDto);
        }
        return baseFileSaveRequestDtos;
    }

    private FileSaveRequestDto transferTo(MultipartFile multipartFile, DirPathType dirPathType) {
        FileSaveRequestDto baseFileSaveRequestDto = null;
        if (multipartFile.isEmpty()) {
            return null;
        }

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            long fileSize = multipartFile.getSize();

            String ext = getFileExtension(originalFilename);
            if (!validateFileExtension(ext)) {
                return null;
            }

            String storedFileName = createStoredFileName(originalFilename);
            String today = getCurrentDateTime();

            String fileUploadPath = createFileUploadDirAndGetPath(dirPathType, today, storedFileName);
            File uploadFile = new File(fileUploadPath);

            multipartFile.transferTo(uploadFile); // upload file to server host dir
            baseFileSaveRequestDto = buildFileSaveRequestDto(dirPathType, originalFilename, storedFileName, fileUploadPath, fileSize, ext);
        } catch (IOException | IllegalStateException | FailSaveFileException e) {
            log.error("[Exception] error occurred, e = {}", e.getMessage());
            throw new FailSaveFileException(ErrorCode.SAVE_FILES);
        }
        return baseFileSaveRequestDto;
    }

    private FileSaveRequestDto buildFileSaveRequestDto(DirPathType dirPathType, String originalFilename, String storedFileName, String fileUploadPath, long fileSize, String ext) {
        switch (dirPathType) {
            case posts:
                return PostFileSaveRequestDto
                        .builder()
                        .originFileName(originalFilename)
                        .storedFileName(storedFileName)
                        .filePath(fileUploadPath)
                        .fileSize(fileSize)
                        .fileExp(ext)
                        .fileAttached("Y")
                        .build();
            case products:
                return ProductFileSaveRequestDto
                        .builder()
                        .originFileName(originalFilename)
                        .storedFileName(storedFileName)
                        .filePath(fileUploadPath)
                        .fileSize(fileSize)
                        .fileExp(ext)
                        .fileAttached("Y")
                        .build();
            default:
                throw new FailSaveFileException(ErrorCode.SAVE_FILES);
        }
    }

    public String createStoredFileName(String originalFilename) {
        return generateUUID() + "." + getFileExtension(originalFilename);
    }

    public String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 디렉토리 경로 yyyy-MM-dd 형태로 생성하기 위해 사용
     */
    private String getCurrentDateTime() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String createFileUploadDirAndGetPath(DirPathType dirPathType, String today, String storedFileName) {
        String uploadFullPath = uploadPath + dirPathType.getDirPathTypeName() + File.separator + today;
        log.debug("서버 파일 업로드 Full Path, uploadFullPath = {}", uploadFullPath);
        createDirectory(uploadFullPath);
        return uploadFullPath + File.separator + storedFileName; // 서버 업로드 경로/UUID.파일명
    }

    private String getFileExtension(String originalFilename) {
        return StringUtils.getFilenameExtension(originalFilename);
    }

    private String createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir.getPath();
    }

    private boolean validateFileExtension(String ext) {
        return FileExtension.isAcceptFileExtension(ext);
    }

    public boolean deleteFiles(List<FileResponseDto> fileResponseDtos) {
        boolean isDeleteSuccess = false;
        for (FileResponseDto fileResponseDto : fileResponseDtos) {
            File deleteFile = new File(fileResponseDto.getFilePath());
            if (!isExistsFile(deleteFile)) { // 파일 경로가 존재하지 않으면 Loop 탈출 후 재 반복
                break;
            }

            isDeleteSuccess = deleteFile(deleteFile);

            if (isDeleteSuccess) {
                log.info("파일 삭제에 성공하였습니다. fileName = {}, isDeleteSuccess = {}", deleteFile.getName(), isDeleteSuccess);
            } else {
                log.info("파일 삭제에 실패하였습니다. isDeleteSuccess = {}", isDeleteSuccess);
            }
        }

        return isDeleteSuccess;
    }

    private boolean isExistsFile(File file) {
        return file.exists();
    }

    private boolean deleteFile(File file) {
        return file.delete();
    }

    public String getSubFileDirPathByDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
    }

    /*public String extractFileDateTime(String filePath) {
        Path path = Paths.get(filePath);
        if (!StringUtils.isEmpty(path.getName(3).toString())) {
            return path.getName(3).toString();
        }
        return "";
    }*/

    public String extractFileDateTimeByFilePath(String filePath) {
        if (filePath.isEmpty()) {
            return "";
        }

        final Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(filePath);
        return matcher.find() ? matcher.group(0) : "";
    }

    public File getDownloadFile(String uploadPath, String domain, String fileCreatedDate, String storedFileName) {
        return new File(uploadPath + domain + File.separator + fileCreatedDate + File.separator + storedFileName);
    }

    public Resource getDownloadFileResource(String uploadPath) {
        return resourceLoader.getResource("file:" + uploadPath);
    }

    public InputStream getDownloadFileInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            log.error("[Occurred Exception] Error Message = {}", ErrorCode.DOWNLOAD_FILES.getMessage());
            throw new FailDownloadFilesException(ErrorCode.DOWNLOAD_FILES);
        }
    }

    public HttpHeaders getHttpHeadersByDownloadFile(FileResponseDto files, Resource resource, InputStream inputStream) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + files.getOriginFileName() + "\"");
            httpHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.contentLength()));
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE.toString());
        } catch (IOException e) {
            log.error("[Occurred Exception] Error Message = {}", ErrorCode.DOWNLOAD_FILES.getMessage());
            throw new FailDownloadFilesException(ErrorCode.DOWNLOAD_FILES);
        }
        return httpHeaders;
    }

    public void responseZipFromAttachments(HttpServletResponse response, List<File> files) {
        // Todo: try with resource 소스 리팩터링 + gzip 안에 있는 파일명은 originalFileName 으로 보여주는게 좋을 것 같으니 추후 수정
        try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
            for (File file : files) {
                if (!file.exists() || !file.isFile()) {
                    continue;
                }
                handleZipOutPutStreamByFile(file, zos);
            }
            zos.finish();
        } catch (IOException e) {
            log.error("[Occurred Exception] Error Message = {}", ErrorCode.DOWNLOAD_FILES.getMessage());
            throw new FailDownloadFilesException(ErrorCode.DOWNLOAD_FILES);
        }
    }

    private void handleZipOutPutStreamByFile(File file, ZipOutputStream zos) throws IOException {
        FileSystemResource fsr = new FileSystemResource(file.getPath());
        ZipEntry zipEntry = new ZipEntry(Objects.requireNonNull(fsr.getFilename()));
        zipEntry.setSize(fsr.contentLength());
        zipEntry.setTime(System.currentTimeMillis());

        zos.putNextEntry(zipEntry);

        StreamUtils.copy(fsr.getInputStream(), zos);
        zos.closeEntry();
    }
}
