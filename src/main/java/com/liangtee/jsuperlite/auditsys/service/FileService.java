package com.liangtee.jsuperlite.auditsys.service;

import com.liangtee.jsuperlite.auditsys.model.FileInfo;
import com.liangtee.jsuperlite.auditsys.model.FileToUser;
import com.liangtee.jsuperlite.auditsys.model.TmpFile;
import com.liangtee.jsuperlite.auditsys.model.User;
import com.liangtee.jsuperlite.auditsys.repository.FileRepository;
import com.liangtee.jsuperlite.auditsys.repository.FileToUserRepository;
import com.liangtee.jsuperlite.auditsys.repository.TmpFileRepository;
import com.liangtee.jsuperlite.auditsys.repository.elasticsearch.ESFileRepository;
import com.liangtee.jsuperlite.auditsys.service.base.BaseService;
import com.liangtee.jsuperlite.auditsys.service.base.PageModel;
import com.liangtee.jsuperlite.auditsys.service.base.QueryHelper;
import com.liangtee.jsuperlite.auditsys.utils.FileUtils;
import com.liangtee.jsuperlite.auditsys.utils.TimeFormater;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * Author: LIANG Tianyi
 * Created by LIANG Tianyi on 2017/5/29.
 * All rights Reserved
 */

@Component("fileService")
@Transactional
public class FileService extends BaseService<FileInfo, String> {

    private FileRepository fileRepository;

    private FileToUserRepository fileToUserRepository;

    private TmpFileRepository tmpFileRepository;

    private ESFileRepository esFileRepository;

    @Autowired
    private QueryHelper queryHelper;

    static final String FOLDER_TYPE = "folder";

    static final String NO_PARENT_FOLDER = "root";

    static final String PATH_SEPARATOR = System.getProperty("file.separator");


    @Autowired
    public FileService(JdbcTemplate jdbcTemplate, FileRepository fileRepository,
                       FileToUserRepository fileToUserRepository,
                       TmpFileRepository tmpFileRepository,
                       ESFileRepository esFileRepository) {
        super(jdbcTemplate);
        this.fileRepository = fileRepository;
        this.fileToUserRepository = fileToUserRepository;
        this.tmpFileRepository = tmpFileRepository;
        this.esFileRepository = esFileRepository;
    }

    public long count() {
        return fileRepository.count();
    }

    public List<FileInfo> search(String keyWord) {
        List<FileInfo> searchResult = new ArrayList<FileInfo>();
        esFileRepository.search(new QueryStringQueryBuilder(keyWord)).forEach(f -> searchResult.add(f));

        return searchResult;
    }

    public FileInfo add(FileInfo fileInfo) {
        esFileRepository.save(fileInfo);
        return fileRepository.save(fileInfo);
    }

    public boolean update(FileInfo fileInfo, String grantedIDs) {
        esFileRepository.save(fileInfo);
        FileInfo file;
        if((file = fileRepository.save(fileInfo)) != null) {
            List<Long> IDList = new ArrayList<Long>();
            Arrays.stream(grantedIDs.split(",")).forEach(ID -> IDList.add(Long.parseLong(ID)));
            return addGrantedList(file.getUUID(), IDList) == null ? false : true;
        }
        return false;
    }

    public Iterable<FileToUser> addGrantedList(String fileID, List<Long> IDList) {

        List<FileToUser> fileToUsers = new ArrayList<FileToUser>();
        IDList.forEach(UID -> fileToUsers.add(new FileToUser(fileID, UID)));

        return fileToUserRepository.save(fileToUsers);
    }

    /**
     * Save the file(inputstream) in the folder of System.getProperty("sys.tmp.upload")
     * and persistent file's information in DB
     *
     * @param in
     * @param code
     * @param fileName
     * @param fileType
     * @return
     */
    public TmpFile addTmpFile(InputStream in, String code, String fileName, String fileType) {

        String tmpFolder = System.getProperty("sys.tmp.upload");
        String dest = tmpFolder + PATH_SEPARATOR + fileName;
        long size = 0L;
        try {
            size = Files.copy(in, Paths.get(tmpFolder + PATH_SEPARATOR + fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tmpFileRepository.save(new TmpFile(code, fileName, fileType, size, dest));
    }

    public boolean delete(String fileID) {
        try {
            FileInfo deletedFile = get(fileID);
            if(deletedFile.getIsFolder() != FileInfo.FOLDER_TYPE) {
                //move to trash folder
                if(FileUtils.moveTo(deletedFile.getFilePath(), System.getProperty("sys.trash") + PATH_SEPARATOR + deletedFile.getFileName())) {
                    esFileRepository.delete(fileID);
                    fileRepository.delete(fileID);
                }

            } else {
                //if the deleted file is folder type
                //move to trash folder
                FileUtils.moveTo(deletedFile.getFilePath(), System.getProperty("sys.trash") + PATH_SEPARATOR + deletedFile.getFileName());
                findAll("PARENT_FOLDER_ID = ?", fileID).forEach(f -> {
                    esFileRepository.delete(f.getUUID());
                    fileRepository.delete(f.getUUID());
                });
                esFileRepository.delete(fileID);
                fileRepository.delete(fileID);

            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public TmpFile getTmiFile(String tmpFileID) {
        return tmpFileRepository.findOne(tmpFileID);
    }

    public boolean deleteTmpFile(String tmpFileID) {
        return  queryHelper.delete(TmpFile.class, "CODE = ?", tmpFileID);
    }

    public FileInfo get(String UUID) {

       List<FileInfo> files = findAll("ID = ?", UUID);
       return files != null && files.size() > 0 ? files.get(0) : null;

    }

    public List<FileInfo> getTreeList(PageModel page) {

        List<FileInfo> result = new LinkedList<FileInfo>();

        List<FileInfo> rootNodes = findByPage(page, null, -1, "PARENT_FOLDER_ID = ?", FileInfo.NO_PARENT_FOLDER);
        Queue<FileInfo> queue = new LinkedList<FileInfo>();
//        queue.addAll(rootNodes);
        int i = 1;
        for(; i<=rootNodes.size(); i++) {
            rootNodes.get(i-1).setSeq(i);
            rootNodes.get(i-1).setpSeq(0);
            queue.add(rootNodes.get(i-1));
        }

        Set<FileInfo> IDs = new HashSet<FileInfo>();

        while (!queue.isEmpty()) {
            FileInfo fileInfo = queue.poll();
            if(!IDs.contains(fileInfo)) {
                result.add(fileInfo);
                IDs.add(fileInfo);
            }
            List<FileInfo> childNodes = findAll("PARENT_FOLDER_ID = ?", fileInfo.getUUID());
//            queue.addAll(childNodes);
            for(int j=0; j<childNodes.size(); j++) {
                childNodes.get(j).setSeq(i++);
                childNodes.get(j).setpSeq(fileInfo.getSeq());
            }
            int parentIndex = result.indexOf(fileInfo);
            result.addAll(parentIndex+1, childNodes);
            IDs.addAll(childNodes);
        }

        return result;
    }

    public List<FileInfo> getTreeList(PageModel page, String sortProperty, int sequence, String queryFileName) {

        if(queryFileName != null && !queryFileName.isEmpty()) {
            List<FileInfo> result = new LinkedList<FileInfo>();
            List<FileInfo> rootNodes = findByPage(page, sortProperty, sequence, "FILE_NAME like ?", queryFileName);
            Queue<FileInfo> queue = new LinkedList<FileInfo>();
//            queue.addAll(rootNodes);
            int i = 1;
            for(; i<=rootNodes.size(); i++) {
                rootNodes.get(i-1).setSeq(i);
                rootNodes.get(i-1).setpSeq(0);
                queue.add(rootNodes.get(i-1));
            }

            Set<FileInfo> IDs = new HashSet<FileInfo>();

            while (!queue.isEmpty()) {
                FileInfo fileInfo = queue.poll();
                if(!IDs.contains(fileInfo)) {
                    result.add(fileInfo);
                    IDs.add(fileInfo);
                }
                List<FileInfo> childNodes = findAll("PARENT_FOLDER_ID = ?", fileInfo.getUUID());
//                queue.addAll(childNodes);
                for(int j=0; j<childNodes.size(); j++) {
                    childNodes.get(j).setSeq(i++);
                    childNodes.get(j).setpSeq(fileInfo.getSeq());
                }
                int parentIndex = result.indexOf(fileInfo);
                result.addAll(parentIndex+1, childNodes);
                IDs.addAll(childNodes);
            }

            return result;
        }

        return null;
    }

    /**
     * @param userID
     * @param page
     * @param sortProperty
     * @param sequence
     * @param queryFileName
     * @return
     */
    public List<FileInfo> findFilesByUser(long userID, PageModel page, String sortProperty, int sequence, String queryFileName) {
        List<FileInfo> result = new ArrayList<FileInfo>();
        Set<String> fileIDs = new HashSet<String>();

        queryHelper.findAll(FileToUser.class, "USER_ID = ?", userID).forEach(f -> fileIDs.add(f.getFileID()));

        if(queryFileName == null) {
            getTreeList(page).forEach(f -> {
                if(f.getSubmitterID() == userID || fileIDs.contains(f.getUUID()))
                    result.add(f);
            });
        } else {
            getTreeList(page, sortProperty, sequence, queryFileName).forEach(f -> {
                if(f.getSubmitterID() == userID || fileIDs.contains(f.getUUID()))
                    result.add(f);
            });
        }

        return result;
    }

    public List<FileInfo> findFilesByUser(long userID, PageModel page) {
        return findFilesByUser(userID, page, "SUBMIT_DATE", -1 ,null);
    }

//    public List<FileInfo> findFilesByUser(long userID, PageModel page) {
//
//        List<FileInfo> result = new ArrayList<FileInfo>();
//        Set<String> fileIDs = new HashSet<String>();
//
//        queryHelper.findAll(FileToUser.class, "USER_ID = ?", userID).forEach(f -> fileIDs.add(f.getFileID()));
//
//        getTreeList(page).forEach(f -> {
//            if(f.getSubmitterID() == userID || fileIDs.contains(f.getUUID()))
//                result.add(f);
//        });
//
//        return result;
//    }

    public FileInfo createFile(User user, String fileName, int isFolder, String parentFolderID, String fileDesc, String grantedUsers,
                           String tmpFileID, String belongToID, int editable) {
        String dirBase = null;

        String savedPath = null;

        FileInfo parentFile = null;

        if(parentFolderID.equalsIgnoreCase(NO_PARENT_FOLDER))
            dirBase = System.getProperty("sys.workplace");
        else {
            parentFile = this.get(parentFolderID);
            dirBase = parentFile.getFilePath();
        }

        List<Long> userIDs = new ArrayList<Long>();
        Arrays.stream(grantedUsers.split(",")).forEach(u->userIDs.add(Long.parseLong(u)));

        if(isFolder == FileInfo.FOLDER_TYPE) {
            savedPath = dirBase + PATH_SEPARATOR + fileName;
            File file = new File(savedPath);

            if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
            file.mkdir();

            FileInfo fileInfo = this.add(new FileInfo(fileName, "文件夹", 0, fileDesc, user.getUID(),
                    user.getName(), belongToID, savedPath, FileInfo.FOLDER_TYPE, parentFolderID.equalsIgnoreCase(NO_PARENT_FOLDER) ?
                    FileInfo.NO_PARENT_FOLDER : parentFolderID, editable));

            this.addGrantedList(fileInfo.getUUID(), userIDs);

            logger.info(String.format("User '%s' created folder '%s', saved path: %s", user.getName(), fileName, savedPath));

            return fileInfo;

        } else {
            try {
                TmpFile tmpFile = this.getTmiFile(tmpFileID);
                savedPath = String.format("%s%s%s.%s", dirBase, PATH_SEPARATOR, fileName, tmpFile.getFileType());
                Files.copy(Paths.get(tmpFile.getFilePath()), Paths.get(savedPath));

                FileInfo fileInfo = this.add(new FileInfo(fileName+"."+tmpFile.getFileType(), tmpFile.getFileType(), tmpFile.getFileSize(), fileDesc, user.getUID(),
                        user.getName(), belongToID, savedPath, FileInfo.FILE_TYPE, parentFolderID.equalsIgnoreCase(NO_PARENT_FOLDER) ?
                        FileInfo.NO_PARENT_FOLDER : parentFolderID, editable));

                this.addGrantedList(fileInfo.getUUID(), userIDs);

                Files.deleteIfExists(Paths.get(tmpFile.getFilePath()));
                this.deleteTmpFile(tmpFileID);

                return fileInfo;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public FileInfo createFile(User user, String fileName, String fileType, int isFolder, String parentFolderID, String fileDesc, String grantedUsers,
                               InputStream inputStream, String belongToID, int editable) {

        String fileCode = String.format("tmp%s%s", TimeFormater.format("yyyyMMddHHmmss")+new Random().nextInt(100), fileType);
        TmpFile tmpFile = addTmpFile(inputStream, fileCode, fileName, fileType);

        return createFile(user, fileName, isFolder, parentFolderID, fileDesc, grantedUsers, tmpFile.getCode(), belongToID, editable);

    }

}
