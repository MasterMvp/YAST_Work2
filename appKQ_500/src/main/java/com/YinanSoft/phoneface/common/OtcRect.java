package com.YinanSoft.phoneface.common;
/* TESO检测定位用的矩形结构体大小：每个12字节 */
public class OtcRect {
	public short nLft;		/* 图像内左上角X坐标，有可能为负值 */
	public short nTop;		/* 图像内左上角Y坐标，有可能为负值 */
	public short uWid;		/* 矩形宽度，0全无效，边界许在图外 */
	public short uHei;		/* 矩形高度，0全无效，边界许在图外 */
	public short uEva;		/* 质量评估分。b15=1为无效，保留用 */
	public short uAgl;		/* 逆时旋转角，b15=1为无效，保留用 */
	@Override
	public String toString() {
		return "OtcRect [nLft=" + nLft + ", nTop=" + nTop + ", uWid=" + uWid
				+ ", uHei=" + uHei + ", uEva=" + uEva + ", uAgl=" + uAgl + "]";
	}
	
};