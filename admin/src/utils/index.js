/**
 * 유닉스 타임스탬프에서 날짜 형식까지  unix2CurrentTime("1497232433000")
 * @param unixTime 유닉스 타임스탬프
 * @return string yyyy-MM-dd HH:mm:ss
 */
export function unix2CurrentTime(unixTime) {
  const date = new Date(parseInt(unixTime))
  const y = date.getFullYear()
  let m = date.getMonth() + 1
  m = m < 10 ? ('0' + m) : m
  let d = date.getDate()
  d = d < 10 ? ('0' + d) : d
  let h = date.getHours()
  h = h < 10 ? ('0' + h) : h
  let minute = date.getMinutes()
  let second = date.getSeconds()
  minute = minute < 10 ? ('0' + minute) : minute
  second = second < 10 ? ('0' + second) : second
  return y + '-' + m + '-' + d + ' ' + h + ':' + minute + ':' + second
}

/**
 * 두 Unix 타임스탬프의 차이점
 * @param unixTimeStart 유닉스 타임스탬프 1
 * @param unixTimeEnd 유닉스 타임스탬프 2
 * @return string xx 시간 | xx 일
 */
export function unixDifference(unixTimeStart, unixTimeEnd) {
  const difference = (unixTimeEnd - unixTimeStart) / 1000
  if (difference >= 86400) {
    return difference / 86400 + '일'
  } else if (difference >= 3600) {
    return difference / 3600 + '시간'
  } else if (difference >= 60) {
    return difference / 60 + '분'
  } else {
    return difference + '초'
  }
}

/**
 * 현재 유닉스 타임스탬프 차이
 * @param unixTimeEnd 유닉스 타임스탬프
 * @return string | null xx일 xx시간 xx분 xx초
 */
export function nowDifference(unixTimeEnd) {
  const unixTimeStart = new Date().getTime()
  const difference = (unixTimeEnd - unixTimeStart) / 1000
  if (difference > 0) {
    let day = Math.floor(difference / (60 * 60 * 24))
    let hour = Math.floor(difference / (60 * 60)) - (day * 24)
    let minute = Math.floor(difference / 60) - (day * 24 * 60) - (hour * 60)
    let second = Math.floor(difference) - (day * 24 * 60 * 60) - (hour * 60 * 60) - (minute * 60)
    if (day <= 9) day = '0' + day
    if (hour <= 9) hour = '0' + hour
    if (minute <= 9) minute = '0' + minute
    if (second <= 9) second = '0' + second
    return day + '일' + hour + '시간' + minute + '분' + second + '초'
  } else {
    return null
  }
}
