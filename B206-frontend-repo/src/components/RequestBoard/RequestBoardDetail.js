import React, { useEffect, useState } from "react";
import axiosApi from "../../api/axiosApi";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import styles from "./RequestBoardDetail.module.css";
import profile from "../../assets/gun.png";
import RequestBoardDelete from "./RequestBoardDelete";
import RequestBoardUpdate from "./RequestBoardUpdate";
import { useSelector } from "react-redux";
import SuggestModal from "../Modal/SuggestModal";

function RequestBoardDetail() {
  const [post, setPost] = useState(null);
  const [img, setImg] = useState(null);
  const [profileImg, setProfileImg] = useState(null);
  const [isModalOpen, setModalOpen] = useState(false);
  const user = useSelector((state) => state.user);
  const hospital = useSelector((state) => state.hospital);
  const customer = useSelector((state) => state.customer);
  const { requestboardSeq } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    console.log("zz", hospital);
    console.log("AFDSADF", user);
    console.log("cust", customer);
    const fetchPost = async () => {
      try {
        let response = await axiosApi.get(
          `/api/requestboard/detail/${requestboardSeq}`
        );
        setPost(response.data); // 먼저 게시글 정보를 설정
        console.log(response.data);

        const customerProfileBase64 = response.data.customerProfileBase64;
        const customerProfileType = response.data.customerProfileType;
        const profileData = `data:${customerProfileType};base64,${customerProfileBase64}`;
        if (customerProfileBase64) {
          setProfileImg(profileData);
        } else {
          setProfileImg(profile);
        }
        const base64 = response.data.base64;
        const type = response.data.type;
        const data = `data:${type};base64,${base64}`;
        if (base64 != null) setImg(data);
      } catch (error) {
        console.log("자유게시판 상세 불러오기 실패: ", error);
      }
    };

    if (requestboardSeq) {
      fetchPost();
    }
  }, []);
  const handleOpenModal = () => {
    setModalOpen(true);
  };

  const handleCloseModal = () => {
    setModalOpen(false);
  };

  const handleSubmitSuggestion = async (message) => {
    console.log("post:", post)
    const requestBody = {
      userSeq: user.userSeq,
      message: message,
    };
    try {
      console.log("보내는거", requestBody);
      await axiosApi.post(
        `/api/requestboard/response/${post.seq}`,
        requestBody
      );
      alert("제안이 성공적으로 전송되었습니다.");
      // 성공 시 추가 동작
    } catch (error) {
      console.error("제안하기 실패: ", error);
      alert("제안을 전송하는 데 실패했습니다.");
    }
  };

  //에러찾을라고..
  if (!post) {
    return <div>Loading...</div>;
  }

  return (
    <div className={styles.FreeBoardContainer}>
      <div className={styles.Head}>
        <img src={profileImg} className={styles.profileImg} />
        <div className={styles.headBox}>
          <div className={styles.headInfo1}>
            <div className={styles.userId}>{post.userName}</div>
            <div className={styles.buttons}>
              <RequestBoardUpdate
                requestboardContent={post.content}
                requestboardTitle={post.title}
                requestboardSeq={post.seq}
                requestboardPart={post.surgeries.map((surgery) => surgery.part)}
              />
              <RequestBoardDelete requestBoardSeq={requestboardSeq} />
            </div>
          </div>
          <div className={styles.headInfo2}>
            <div>{post.userEmail}</div>
            <div>작성 날짜: {post.regDate}</div>
          </div>
        </div>
      </div>
      <div className={styles.title}>{post.title}</div>
      <div className={styles.contents}>
        {/* <img src={img} alt="게시글 이미지" /> */}
        {img && (
          <img src={img} alt="게시글 이미지" className={styles.contentImg} />
        )}
        <div className={styles.horizon} />
        <div className={styles.contentText}>
          <div>{post.content}</div>
        </div>
      </div>
      {/* <div>해시태그: {post.hashTag}</div> */}
      <button className={styles.suggestButton} onClick={handleOpenModal}>
        제안하기
      </button>
      <SuggestModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onSubmit={handleSubmitSuggestion}
      />
    </div>
  );
}
export default RequestBoardDetail;
//뜬다!!
