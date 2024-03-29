import { NavLink, Route, Routes } from "react-router-dom";
import styles from "./HospitalMyPage.module.css";
import { useSelector } from "react-redux";
import Doctor from "./Doctor";
import consult from "../../../assets/mypage/consult.png";
import reserve from "../../../assets/mypage/reserve.png";
import HospitalInfo from "./HospitalInfo";
import HospitalInfoUpdate from "./HospitalInfoUpdate";
import doctor from "../../../assets/mypage/doctor.png";
import ReservationList from "./ReservationList";
import ConsultingList from "./ConsultingList";
import { useEffect } from "react";

function HospitalMyPageForm() {
  const user = useSelector((state) => state.user);
  const hospital = useSelector((state) => state.hospital);
  useEffect(() => {
    console.log(user);
    console.log(hospital);
  });
  return (
    <div className={styles.mypageContainer}>
      <NavLink to="info" className={styles.profile}>
        <img src={hospital.profileImg} className={styles.profileImage} />
        <div className={styles.profileInfo}>
          <div className={styles.profileName}>{hospital.hospitalInfo_name}</div>
          <div className={styles.profileEmail}>
            {hospital.hospitalInfo_email}
          </div>
        </div>
      </NavLink>
      <div className={styles.sidebar}>
        <nav className={styles.navigation}>
          <div className={styles.column}>
            <img src={doctor} className={styles.icon} />
            <NavLink
              to="doctor"
              className={({ isActive }) =>
                isActive ? `${styles.navLink} ${styles.active}` : styles.navLink
              }
            >
              의사
            </NavLink>
          </div>
          <div className={styles.horizon}></div>
          <div className={styles.column}>
            <img src={reserve} alt="reserve" className={styles.icon} />
            <NavLink
              to="reserve"
              className={({ isActive }) =>
                isActive ? `${styles.navLink} ${styles.active}` : styles.navLink
              }
            >
              상담 일정
            </NavLink>
          </div>
          <div className={styles.horizon}></div>
          <div className={styles.column}>
            <img src={consult} alt="reserve" className={styles.icon} />
            <NavLink
              to="consult"
              className={({ isActive }) =>
                isActive ? `${styles.navLink} ${styles.active}` : styles.navLink
              }
            >
              상담 내역
            </NavLink>
          </div>
        </nav>
      </div>
      <div className={styles.contents}>
        <Routes>
          <Route path="info" element={<HospitalInfo />} />
          <Route path="info/update" element={<HospitalInfoUpdate />} />
          <Route path="doctor" element={<Doctor />} />
          <Route path="reserve" element={<ReservationList />} />
          <Route path="consult" element={<ConsultingList />} />
        </Routes>
      </div>
    </div>
  );
}

export default HospitalMyPageForm;
