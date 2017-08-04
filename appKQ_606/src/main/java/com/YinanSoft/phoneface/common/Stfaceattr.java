package com.YinanSoft.phoneface.common;

public class Stfaceattr {

	public Stfaceattr() {
		setSize(115 * 4);
		// locFace = new int[4];
		// locPoint = new int[49][2];
		// locEye = new int[2][2];
		// eyeDegree = 0;
		// mouthDegree = 0;
		// headPosi = new int[3];
		// age = 0;
		// gender = 0;
	}

	int size; /* 本结构的大小，sizeof(STFACEATTR) 114 */
	int[] locFace = new int[4]; /* 人脸的坐标(XYWH宽或高为0则无效) */
	int[][] locPoint = new int[49][2]; /* 49个点的XY坐标(0=49点，1=29点) */
	int[][] locEye = new int[2][2]; /* 0=左眼XY，1=右眼XY */
	int eyeDegree; /* 人眼的睁开程度[0,100] */
	int mouthDegree; /* 嘴巴的张开程度[0,100] */
	int[] headPosi = new int[3]; /* 歪头[1=Y,0=N]-转头-抬低头，[-90,90] */
	int age; /* 给0不返回，否则检测年龄[1, 100] */
	int gender; /* 给0不返回，否则检测性别(1=男，2=女) */
	int occlusion;/* 遮挡 ，1=不遮 ，2=挡 。当 glasses 初始化为 0 时 ，不 进 行 遮挡 判 定。 */
	int[] eyeTwoDegree = new int[2];/* [0]左，[1]右，人眼的睁开程度[0,100] */
	int fftSharpness;/* 给0不返回，否则检测清晰度[1,255] */

	/**
	 * 新加属性
	 */
	int lsdIsPhoto;/* 给0不判断，否则返回0=照片，1=不是照片 */
	int hsvSkinRate;/* 给0不判断，否则肤色占比，<30为灰度脸 */
	int rotEyeBall;/* 给0不返回，眼球偏转度[0,100]，50居中 */
	int faceLightness;/* 给0不返回，脸部的亮度[0,100]，50居中 */
	int faceUniformity;/* 给0不返回，脸部均匀度[0,100]，50居中 */
	int ratLiuHair;/* 给0不返回，额头刘海[0,100]，50居中 */
	int rateSmile;/* 给0不返回，大笑的程度[0,100]，50居中 */
	int isFrown;/* 给0不返回，是否有皱纹[0,1] */


	// int[] hFattr = new int[(1 + 4 + 49 * 2 + 2 * 2 + 1 + 1 + 3 + 1 + 1)*4];
	int[] hFattr = new int[1 + 4 + 49 * 2 + 2 * 2 + 1 + 1 + 3 + 1 + 1 + 1 + 2 + 1 + 8];

	public int[] gethFattr() {
		// size
		hFattr[0] = getSize();
		// int locFace[4];
		hFattr[1] = locFace[0];
		hFattr[2] = locFace[1];
		hFattr[3] = locFace[2];
		hFattr[4] = locFace[3];
		// int locPoint[49][2];
		int j = 4;
		for (int i = 0; i < locPoint.length; i++) {
			for (int m = 0; m < 2; m++) {
				hFattr[++j] = locPoint[i][m];
			}
		}
		int n = 102;
		// int locEye[2][2];
		for (int i = 0; i < 2; i++) {
			for (int k = 0; k < 2; k++) {
				hFattr[++n] = locEye[i][k];
			}
		}
		hFattr[107] = eyeDegree;
		hFattr[108] = mouthDegree;

		hFattr[109] = headPosi[0];
		hFattr[110] = headPosi[1];
		hFattr[111] = headPosi[2];
		hFattr[112] = age;
		hFattr[113] = gender;
		hFattr[114] = occlusion;

		hFattr[115] = eyeTwoDegree[0];
		hFattr[116] = eyeTwoDegree[1];
		hFattr[117] = fftSharpness;

		hFattr[118] = lsdIsPhoto;
		hFattr[119] = hsvSkinRate;
		hFattr[120] = rotEyeBall;
		hFattr[121] = faceLightness;
		hFattr[122] = faceUniformity;
		hFattr[123] = ratLiuHair;
		hFattr[124] = rateSmile;
		hFattr[125] = isFrown;
		return hFattr;
	}

	public int getSize() {
		this.size = hFattr[0];
		return size;
	}

	public void setSize(int size) {
		hFattr[0] = size;
	}

	public int[] getLocFace() {
		this.locFace[0] = hFattr[1];
		this.locFace[1] = hFattr[2];
		this.locFace[2] = hFattr[3];
		this.locFace[3] = hFattr[4];
		return locFace;
	}

	public void setLocFace(int x, int y, int w, int h) {
		this.locFace[0] = x;
		this.locFace[1] = y;
		this.locFace[2] = w;
		this.locFace[3] = h;

		hFattr[1] = this.locFace[0];
		hFattr[2] = this.locFace[1];
		hFattr[3] = this.locFace[2];
		hFattr[4] = this.locFace[3];
	}

	public int[][] getLocPoint() {
		int m = 5;
		for (int i = 0; i < 49; i++) {
			for (int j = 0; j < 2; j++) {
				locPoint[i][j] = hFattr[m++];
			}
		}
		return locPoint;
	}

	public void setLocPoint(int[][] locPoint) {
		for (int i = 0; i < locPoint.length; i++) {
			for (int j = 0; j < 2; j++) {
				locPoint[i][j] = 0;
			}
		}
		this.locPoint = locPoint;
		for (int i = 0; i < locPoint.length; i++) {

		}
	}

	public int[][] getLocEye() {
		this.locEye[0][0] = hFattr[103];
		this.locEye[0][1] = hFattr[104];
		this.locEye[1][0] = hFattr[105];
		this.locEye[1][1] = hFattr[106];
		return locEye;
	}

	public void setLocEye(int l0, int l1, int r0, int r1) {
		hFattr[103] = l0;
		hFattr[104] = l1;
		hFattr[105] = r0;
		hFattr[106] = r1;
	}

	public int getEyeDegree() {
		this.eyeDegree = hFattr[107];
		return this.eyeDegree;
	}

	public void setEyeDegree(int eyeDegree) {
		hFattr[107] = eyeDegree;
	}

	public int getMouthDegree() {
		this.mouthDegree = hFattr[108];
		return this.mouthDegree;
	}

	public void setMouthDegree(int mouthDegree) {
		hFattr[108] = mouthDegree;
	}

	public int[] getHeadPosi() {
		this.headPosi[0] = hFattr[109];
		this.headPosi[1] = hFattr[110];
		this.headPosi[2] = hFattr[111];
		return this.headPosi;
	}

	/**
	 * @param shake 歪头
	 * @param circl 转头
	 * @param up    抬头
	 */
	public void setHeadPosi(int shake, int turn, int up) {
		this.headPosi[0] = shake;
		this.headPosi[1] = turn;
		this.headPosi[2] = up;

		hFattr[109] = this.headPosi[0];
		hFattr[110] = this.headPosi[1];
		hFattr[111] = this.headPosi[2];
	}

	public int getAge() {
		this.age = hFattr[112];
		return this.age;
	}

	public void setAge(int age) {
		hFattr[112] = age;
	}

	public int getGender() {
		this.gender = hFattr[113];
		return gender;
	}

	public void setGender(int gender) {
		hFattr[113] = gender;
	}

	public int getOcclusion() {
		this.occlusion = hFattr[114];
		return occlusion;
	}

	public void setOcclusion(int occlusion) {
		hFattr[114] = occlusion;
	}

}
