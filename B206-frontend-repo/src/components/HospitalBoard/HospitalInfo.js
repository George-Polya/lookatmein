import React, { useState, useEffect } from "react";
import axiosApi from "../../api/axiosApi";
import { useParams, useNavigate } from "react-router-dom";
// import Box from "@mui/material/Box";
// import div from "@mui/material/div";
import Reserve from "../Modal/DateTimePickerModal";
import { useDispatch } from "react-redux";
import styles from "./HospitalInfo.module.css";
import basicHos from "../../assets/basicHos.png";
import profile from "../../assets/gun.png";
import StarResult from "../ReviewBoard/StarRating/StarResult.js";
import { useSelector } from "react-redux";
import Wish from "./HospitalWish.js";

const HospitalInfo = () => {
  const dispatch = useDispatch();
  const { hospitalInfo_seq } = useParams();
  const [hospitalData, setHospitalData] = useState({
    hospitalInfo_seq: "",
    hospitalInfo_name: "",
    hospitalInfo_phoneNumber: "",
    hospitalInfo_introduce: "",
    hospitalInfo_address: "",
    hospitalInfo_open: "",
    hospitalInfo_close: "",
    hospitalInfo_url: "",
    userSeq: "",
    hospitalInfo_avgScore: "",
    hospitalInfo_cntReviews: "",
  });

  const [img, setImg] = useState(null);
  useEffect(() => {
    const getHospitalInfo = async () => {
      try {
        console.log("hospitalInfo_seq: ", hospitalInfo_seq);

        const response = await axiosApi.get(
          `/api/hospital-info/detail/${hospitalInfo_seq}`
        );
        console.log("여기", response.data);
        setHospitalData(response.data);
        console.log(response.data.userSeq);
        // const imgResponse = await axiosApi.get(response.data.fileUrl);
        // console.log("response2: ", imgResponse);
        // const base64 = imgResponse.data.base64;
        // const type = imgResponse.data.type;
        // const data = `data:${type};base64,${base64}`;
        // setImg(data);
      } catch (error) {
        console.error("병원 정보를 가져오는데 실패했습니다:", error);
      }
    };
    getHospitalInfo();
  }, []);

  const userSeq = useSelector((state) => state.hospital.hospitalSeq);
  const navigate = useNavigate();
  const [reviews, setReviews] = useState([]);

  useEffect(() => {
    axiosApi
      .get(`/api/hospital-info/reviews/${hospitalInfo_seq}`)
      .then((response) => {
        setReviews(response.data);
      })
      .catch((error) => {
        console.log("병원별 후기 목록 불러오기 실패", error);
      });
  }, []);

  const handleClick = (reviewBoard_seq) => {
    navigate(`/reviewdetail/${reviewBoard_seq}`);
  };

  const [doctors, setDoctors] = useState([]);
  useEffect(() => {
    console.log("병원정보", hospitalData);
    axiosApi
      .get(`api/hospital-info/doctors/${hospitalInfo_seq}`)
      .then((response) => {
        setDoctors(response.data);
      })
      .catch((error) => {
        console.log("의사 목록 불러오기 실패 : ", error);
      });
  }, []);

  const viewDoctorInfo = (docInfoSeq) => {
    navigate(`/의사 디테일 하나..?`);
  };

  const handleSearch = (searchTerm) => {
    navigate(`/search/${searchTerm}`);
  };

  const week = ["월", "화", "수", "목", "금", "토", "일"];
  return (
    <div className={styles.container}>
      <div className={styles.part1}>
        <div className={styles.imgTitle}>
          <div className={styles.profile}>
            {img ? (
              <img src={img} alt="게시글 이미지" />
            ) : (
              <img src={basicHos} alt="병원 기본 프사" />
            )}
          </div>
          <div className={styles.title}>{hospitalData.hospitalInfo_name}</div>
          <Wish />
        </div>
        <div className={styles.address}>
          <div className={styles.tt}>주소</div>
          <div className={styles.text}>{hospitalData.hospitalInfo_address}</div>
        </div>
        <div className={styles.url}>
          <div className={styles.tt}>홈페이지 </div>
          <div className={styles.link}>{hospitalData.hospitalInfo_url}</div>
        </div>
        <div className={styles.time}>
          <div className={styles.tt}>진료시간</div>
          <div className={styles.weeks}>
            {week.map((day) => (
              <div key={day} className={styles.day}>
                {day === "토" && (
                  <div className={styles.day}>
                    {day} {hospitalData.hospitalInfo_open} ~ 13:00{" "}
                  </div>
                )}
                {day === "일" && <div>{day} 휴무</div>}
                {!(day === "토" || day === "일") && (
                  <div className={styles.day}>
                    {day} {hospitalData.hospitalInfo_open} ~{" "}
                    {hospitalData.hospitalInfo_close}{" "}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
        <div className={styles.Number}>
          <div className={styles.tt}>전화번호</div>
          <div className={styles.text}>
            {hospitalData.hospitalInfo_phoneNumber}
          </div>
        </div>
        <div className={styles.info}>
          <div className={styles.tt}>소개</div>
          <div className={styles.text}>
            {hospitalData.hospitalInfo_introduce}
          </div>
        </div>

        <div className={styles.score}>
          <StarResult score={hospitalData.hospitalInfo_avgScore} />(
          {hospitalData.hospitalInfo_avgScore},
          {hospitalData.hospitalInfo_cntReviews})
        </div>
        <Reserve hospitalInfoSeq={hospitalInfo_seq} />
      </div>
      <div className={styles.part2}>
        {reviews.map((review) => (
          <li
            key={review.reviewBoard_seq}
            className={styles.reviewItem}
            onClick={() => handleClick(review.reviewBoard_seq)}
          >
            <div>
              <img src={profile} alt="프로필" className={styles.profile} />
            </div>
            <div className={styles.writer}>
              <div>{review.customer_name}</div>
              <div>
                <div>
                  <StarResult score={review.reviewBoard_score} />
                </div>
              </div>
            </div>
            <div className={styles.title}>
              <div>{review.reviewBoard_title}</div>
            </div>
            <div className={styles.prices}>
              <div className={styles.price}>
                견적가 : {review.reviewBoard_expected_price} 원
              </div>
              <div className={styles.price}>
                시술가 : {review.reviewBoard_surgery_price} 원
              </div>
            </div>
          </li>
        ))}
      </div>
      <div className={styles.part3}>
        {doctors.map((doctor) => (
          <li
            key={doctor.doctorSeq}
            className={styles.doctorItem}
            onClick={() => viewDoctorInfo}
          >
            <div>
              <img src={profile} alt="프로필" className={styles.profile} />
            </div>
            <div className={styles.doctor}>
              <div className={styles.writer}>
                <div>
                  <span className={styles.docName}>{doctor.doctorName}</span>{" "}
                  원장
                </div>
              </div>
              <div className={styles.hashtagButton}>
                {doctor.doctorCategory.map((category, index) => (
                  <div key={index} onClick={() => handleSearch(category.part)}>
                    {category.part}
                  </div>
                ))}
              </div>
            </div>
          </li>
        ))}
      </div>
    </div>
  );
};

export default HospitalInfo;
