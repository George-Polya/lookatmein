import { useEffect, useRef } from "react"
import { useSelector } from "react-redux"
import * as RTC from "../../utils"
const Peer = ()=>{
    const {localStream,remoteStream}=useSelector((state)=>state.rtc)
    const localVideoRef=useRef()
    const removeVideoRef=useRef()
      useEffect(()=>{
          RTC.connectWithWebSocket()
          RTC.connectWithPeer()
          RTC.getLocalStream()
      },[])
    useEffect(()=>{
      if(localStream && localStream.id){
        localVideoRef.current.srcObject=localStream
        localVideoRef.current.onloadmetadata=()=>{
          localVideoRef.current.play()
        }
      }
    },[localStream])
    useEffect(()=>{
      if(remoteStream && remoteStream.id){
        removeVideoRef.current.srcObject=remoteStream
        removeVideoRef.current.onloadmetadata=()=>{
          removeVideoRef.current.play()
        }
      }
    },[remoteStream])
    return (
      <div className="App">
      
        <div>
          local video
      <video autoPlay muted width={400} height={400} ref={localVideoRef} />
        </div>
        <button onClick={()=>{
        RTC.callRequest({
          socketId:window.mySocketId,
          peerId:window.myPeer
        })
      }}>hello</button>
        <div>
          remote video
       <video autoPlay muted width={400} height={400} ref={removeVideoRef} />
        </div>
      </div>
    );
}

export default Peer