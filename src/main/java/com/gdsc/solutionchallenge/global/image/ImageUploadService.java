package com.gdsc.solutionchallenge.global.image;

import com.gdsc.solutionchallenge.board.entity.PostPhoto;
import com.gdsc.solutionchallenge.board.repository.PostPhotoRepository;
import com.gdsc.solutionchallenge.global.exception.ApiException;
import com.gdsc.solutionchallenge.global.exception.ApiResponseStatus;
import com.gdsc.solutionchallenge.meetup.entity.MeetupPhoto;
import com.gdsc.solutionchallenge.meetup.repository.MeetupPhotoRepository;
import com.gdsc.solutionchallenge.user.entity.User;
import com.gdsc.solutionchallenge.user.repository.UserRepository;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImageUploadService {

    private final Storage storage;
    private final UserRepository userRepository;
    private final MeetupPhotoRepository meetupPhotoRepository;
    private final PostPhotoRepository postPhotoRepository;
    private String baseurl = "https://storage.googleapis.com/bebe0";

    // 버킷이름 따로 properties에 저장했음
    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    public GetGDSRes uploadSingleImage(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString(); // 파일명 중복 방지
        String ext = file.getContentType();
        try {
            BlobInfo blobInfo = storage.create(
                    BlobInfo.newBuilder(bucketName, fileName)
                            .setContentType(ext)
                            .build(),
                    file.getInputStream());
        } catch(IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }
        GetGDSRes getGDSRes = new GetGDSRes(baseurl+"/"+fileName, fileName);
        return getGDSRes;
    }


    public List<GetGDSRes> uploadImage(List<MultipartFile> multipartFiles) {
        List<GetGDSRes> fileList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileList에 추가
        multipartFiles.forEach(file -> {
            String fileName = UUID.randomUUID().toString(); // 파일명 중복 방지
            String ext = file.getContentType();
            try {
                BlobInfo blobInfo = storage.create(
                        BlobInfo.newBuilder(bucketName, fileName)
                                .setContentType(ext)
                                .build(),
                        file.getInputStream());
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
            fileList.add(new GetGDSRes(baseurl+"/"+fileName, fileName));
        });
        return fileList;

    }

    public void deleteImage(String filename) {

        PostPhoto postPhoto = postPhotoRepository.findByFileName(filename).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.BAD_REQUEST);
        });

        Blob blob = storage.get(bucketName, postPhoto.getFileName());
        Storage.BlobSourceOption precondition = Storage.BlobSourceOption.generationMatch(blob.getGeneration());
        storage.delete(bucketName, postPhoto.getFileName(), precondition);
    }

    public void deleteImageMeetup(String filename) {

        MeetupPhoto meetupPhoto = meetupPhotoRepository.findByFileName(filename).orElseThrow(() -> {
            throw new ApiException(ApiResponseStatus.BAD_REQUEST);
        });

        Blob blob = storage.get(bucketName, meetupPhoto.getFileName());
        Storage.BlobSourceOption precondition = Storage.BlobSourceOption.generationMatch(blob.getGeneration());
        storage.delete(bucketName, meetupPhoto.getFileName(), precondition);
    }
}