import React, { useState, useEffect } from "react";
import axiosApi from "../../../api/axiosApi";
import { useSelector } from "react-redux"; // useSelector를 불러옵니다.

function ReservationList() {
  // Redux store에서 userId를 가져옵니다.
  const userSeq = useSelector((state) => state.user.userSeq);

  const [reservations, setReservations] = useState([]);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        const response = await axiosApi.get(`/api/reserve/user/${userSeq}`); // 병원용으로 바꿔야함
        console.log(response.data);
        setReservations(response.data);
      } catch (error) {
        console.error("Error fetching reservations:", error);
      }
    };

    fetchReservations();
  }, [userSeq]);

  return (
    <div>
      <h2>Reservation List</h2>
      <ul>
        {reservations.map((reservation, index) => (
          <li key={index}>
            <p>Customer Name: {reservation.customerName}</p>
            <p>Hospital Name: {reservation.hospitalName}</p>
            <p>
              Reservation Type:{" "}
              {reservation.reserveType === 0 ? "First-time" : "Follow-up"}
            </p>
            <p>
              Date: {reservation.year}-
              {String(reservation.month).padStart(2, "0")}-
              {String(reservation.day).padStart(2, "0")}
            </p>
            <p>Day of Week: {reservation.dayofweek}</p>
            <p>
              Time:{" "}
              {reservation.time < 12
                ? `${reservation.time} AM`
                : `${
                    reservation.time === 12
                      ? reservation.time
                      : reservation.time - 12
                  } PM`}
            </p>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default ReservationList;