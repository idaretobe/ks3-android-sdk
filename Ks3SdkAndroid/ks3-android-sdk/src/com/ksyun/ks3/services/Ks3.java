package com.ksyun.ks3.services;

import java.io.File;
import java.util.List;

import android.content.Context;

import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
import com.ksyun.ks3.model.result.CopyResult;
import com.ksyun.ks3.model.result.HeadObjectResult;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CopyObjectResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.HeadObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.CopyObjectRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.HeadObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;

public abstract interface Ks3 {
	
	public Ks3HttpRequest getObject(Context context, String bucketname, String key,
			GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler getObjectResponceHandler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey, File file,
			PutObjectResponseHandler handler);

	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler);
	
	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler);

	public void headObject(String bucketname, String objectkey,
			HeadObjectResponseHandler resultHandler);

	public HeadObjectResult syncHeadObject(String bucketname, String objectkey)throws Throwable ;
	
	public void headObject(HeadObjectRequest request,
			HeadObjectResponseHandler resultHandler);

	public HeadObjectResult syncHeadObject(HeadObjectRequest request)throws Throwable ;
	
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CopyObjectResponseHandler handler);

	public CopyResult syncCopyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey) throws Throwable;
	
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl, CopyObjectResponseHandler handler);
	
	public CopyResult syncCopyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl) throws Throwable;
	
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			AccessControlList accessControlList,
			CopyObjectResponseHandler handler);

	public CopyResult syncCopyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			AccessControlList accessControlList) throws Throwable;
	
	public void copyObject(CopyObjectRequest request,
			CopyObjectResponseHandler handler);

	public CopyResult syncCopyObject(CopyObjectRequest request) throws Throwable;
	
	public void initiateMultipartUpload(String bucketname, String objectkey,
			InitiateMultipartUploadResponceHandler resultHandler);
	
	public InitiateMultipartUploadResult syncInitiateMultipartUpload(String bucketname, String objectkey)throws Throwable;

	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler);

	public InitiateMultipartUploadResult syncInitiateMultipartUpload(InitiateMultipartUploadRequest request)throws Throwable;
	
	public void uploadPart(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize,
			UploadPartResponceHandler resultHandler);

	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler);

	public void completeMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags,
			CompleteMultipartUploadResponseHandler handler);
	
	public CompleteMultipartUploadResult syncCompleteMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags)throws Throwable;
	
	public void completeMultipartUpload(ListPartsResult result,
			CompleteMultipartUploadResponseHandler handler);
	
	public CompleteMultipartUploadResult syncCompleteMultipartUpload(ListPartsResult result)throws Throwable;
	
	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler);

	public CompleteMultipartUploadResult syncCompleteMultipartUpload(CompleteMultipartUploadRequest request)throws Throwable;
	
	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId, AbortMultipartUploadResponseHandler handler);

	public void syncAbortMultipartUpload(String bucketname, String objectkey,String uploadId)throws Throwable;
	
	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler);

	public void syncAbortMultipartUpload(AbortMultipartUploadRequest request)throws Throwable;
	
	public void listParts(String bucketname, String objectkey, String uploadId,
			ListPartsResponseHandler handler);

	public ListPartsResult syncListParts(String bucketname, String objectkey, String uploadId)throws Throwable;
	
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, ListPartsResponseHandler handler);

	public ListPartsResult syncListParts(String bucketname, String objectkey, String uploadId,
			int maxParts)throws Throwable;
	
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker, ListPartsResponseHandler handler);

	public ListPartsResult syncListParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker)throws Throwable;
	
	public void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler);

	public ListPartsResult syncListParts(ListPartsRequest request)throws Throwable;
	
	public void pause(Context context);

	public void cancel(Context context);

	public Context getContext();

}
