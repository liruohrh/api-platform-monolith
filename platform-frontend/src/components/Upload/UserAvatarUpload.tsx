import React, { useState } from 'react';
import { GetProp, notification, Upload, UploadFile, UploadProps } from 'antd';
import { LoadingOutlined, PlusOutlined } from '@ant-design/icons';
import { getBackendBaseURL } from '@/constants';
type FileType = Parameters<GetProp<UploadProps, 'beforeUpload'>>[0];
import ImgCrop from 'antd-img-crop';



const beforeUpload = (file: FileType) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    notification.error({ message: 'You can only upload JPG/PNG file!' });
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    notification.error({ message: 'Image must smaller than 2MB!' });
  }
  return isJpgOrPng && isLt2M;
};
const uploadFilename = "image.png";
export const UserAvatarUpload: React.FC<{
  avatarUrl?: string,
  setAvatarUrl: (val: string) => void
}> = ({ avatarUrl, setAvatarUrl }) => {
  console.log("更新 UserAvatarUpload");
  const [loading, setLoading] = useState(false);
  const [fileList, setFileList] = useState<UploadFile[]>([
    {
      uid: uploadFilename,
      name: uploadFilename,
      status: 'done',
      url: avatarUrl ?? "",
    }
  ]);

  const handleChange: UploadProps['onChange'] = (info) => {
    const newFileList: UploadFile[] = [{
      uid: info.file.uid,
      name: info.file.name,
      status: info.file.status
    }];

    if (info.file.status === 'uploading') {
      setLoading(true);
      setFileList(newFileList);
      return;
    }
    if (info.file.status === 'removed') {
      setFileList([]);
      return;
    }
    setLoading(false);
    if (info.file.status === 'done' && info.file.response) {
      setAvatarUrl(info.file.response);
      setFileList(info.fileList);
      return;
    }
    newFileList[0].url = avatarUrl;
    setFileList(newFileList);
    console.log(info);
    let uploadSize = info.file.size ?? 0;
    let uploadSizeUnit = "B";
    if (uploadSize > 1024 && uploadSize / 1024 > 512) {
      uploadSize /= 1024;
      uploadSizeUnit = "KB";
    }
    if (uploadSize > 1024 && uploadSize / 1024 > 512) {
      uploadSize /= 1024;
      uploadSizeUnit = "MB";
    }
    if (uploadSize > 1024 && uploadSize / 1024 > 512) {
      uploadSize /= 1024;
      uploadSizeUnit = "GB";
    }
    notification.warning({ message: info.file.response ? info.file.response.msg : "上传失败, 恢复原来的，允许最大上传1MB，当前文件" + (uploadSize.toFixed(3) + uploadSizeUnit) });
  };
  const uploadButton = (
    <button style={{ border: 0, background: 'none' }} type="button">
      {loading ? <LoadingOutlined /> : <PlusOutlined />}
      <div style={{ marginTop: 8 }}>Upload</div>
    </button>
  );

  return (
    <ImgCrop
      rotationSlider
      showReset
      cropShape={'round'}
      modalTitle={"编辑头像"}
    >
      <Upload
        action={`${getBackendBaseURL()}/oss`}
        name="file"
        method={"POST"}
        multiple={true}
        withCredentials={true}
        listType="picture-circle"
        fileList={fileList}
        onChange={handleChange}
        beforeUpload={beforeUpload}
        showUploadList={{
          showPreviewIcon: false
        }}
      >
        {fileList.length === 0 && uploadButton}
      </Upload>
    </ImgCrop >
  );
};

export default UserAvatarUpload;
