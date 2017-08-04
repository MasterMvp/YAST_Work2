/*     */ package com.smartshino.face;
/*     */ 
/*     */ public class Stfaceattr
/*     */ {
/*     */   int size;
/*  18 */   int[] locFace = new int[4];
/*  19 */   int[][] locPoint = new int[49][2];
/*  20 */   int[][] locEye = new int[2][2];
/*     */   int eyeDegree;
/*     */   int mouthDegree;
/*  23 */   int[] headPosi = new int[3];
/*     */   int age;
/*     */   int gender;
/*     */   int occlusion;
/*  28 */   int[] hFattr = new int[115];
/*     */ 
/*     */   public Stfaceattr()
/*     */   {
/*   6 */     setSize(460);
/*     */   }
/*     */ 
/*     */   public int[] gethFattr()
/*     */   {
/*  32 */     this.hFattr[0] = getSize();
/*     */ 
/*  34 */     this.hFattr[1] = this.locFace[0];
/*  35 */     this.hFattr[2] = this.locFace[1];
/*  36 */     this.hFattr[3] = this.locFace[2];
/*  37 */     this.hFattr[4] = this.locFace[3];
/*     */ 
/*  39 */     int j = 4;
/*  40 */     for (int i = 0; i < this.locPoint.length; ++i) {
/*  41 */       for (int m = 0; m < 2; ++m) {
/*  42 */         this.hFattr[(++j)] = this.locPoint[i][m];
/*     */       }
/*     */     }
/*  45 */     int n = 102;
/*     */ 
/*  47 */     for (int i = 0; i < 2; ++i) {
/*  48 */       for (int k = 0; k < 2; ++k) {
/*  49 */         this.hFattr[(++n)] = this.locEye[i][k];
/*     */       }
/*     */     }
/*  52 */     this.hFattr[107] = this.eyeDegree;
/*  53 */     this.hFattr[108] = this.mouthDegree;
/*     */ 
/*  55 */     this.hFattr[109] = this.headPosi[0];
/*  56 */     this.hFattr[110] = this.headPosi[1];
/*  57 */     this.hFattr[111] = this.headPosi[2];
/*  58 */     this.hFattr[112] = this.age;
/*  59 */     this.hFattr[113] = this.gender;
/*  60 */     this.hFattr[114] = this.occlusion;
/*  61 */     return this.hFattr;
/*     */   }
/*     */ 
/*     */   public int getSize() {
/*  65 */     this.size = this.hFattr[0];
/*  66 */     return this.size;
/*     */   }
/*     */ 
/*     */   public void setSize(int size) {
/*  70 */     this.hFattr[0] = size;
/*     */   }
/*     */ 
/*     */   public int[] getLocFace() {
/*  74 */     this.locFace[0] = this.hFattr[1];
/*  75 */     this.locFace[1] = this.hFattr[2];
/*  76 */     this.locFace[2] = this.hFattr[3];
/*  77 */     this.locFace[3] = this.hFattr[4];
/*  78 */     return this.locFace;
/*     */   }
/*     */ 
/*     */   public void setLocFace(int x, int y, int w, int h) {
/*  82 */     this.locFace[0] = x;
/*  83 */     this.locFace[1] = y;
/*  84 */     this.locFace[2] = w;
/*  85 */     this.locFace[3] = h;
/*     */ 
/*  87 */     this.hFattr[1] = this.locFace[0];
/*  88 */     this.hFattr[2] = this.locFace[1];
/*  89 */     this.hFattr[3] = this.locFace[2];
/*  90 */     this.hFattr[4] = this.locFace[3];
/*     */   }
/*     */ 
/*     */   public int[][] getLocPoint() {
/*  94 */     int m = 5;
/*  95 */     for (int i = 0; i < 49; ++i) {
/*  96 */       for (int j = 0; j < 2; ++j) {
/*  97 */         this.locPoint[i][j] = this.hFattr[(m++)];
/*     */       }
/*     */     }
/* 100 */     return this.locPoint;
/*     */   }
/*     */ 
/*     */   public void setLocPoint(int[][] locPoint) {
/* 104 */     for (int i = 0; i < locPoint.length; ++i) {
/* 105 */       for (int j = 0; j < 2; ++j) {
/* 106 */         locPoint[i][j] = 0;
/*     */       }
/*     */     }
/* 109 */     this.locPoint = locPoint;
/* 110 */     for (int i = 0; i < locPoint.length; ++i);
/*     */   }
/*     */ 
/*     */   public int[][] getLocEye()
/*     */   {
/* 116 */     this.locEye[0][0] = this.hFattr[103];
/* 117 */     this.locEye[0][1] = this.hFattr[104];
/* 118 */     this.locEye[1][0] = this.hFattr[105];
/* 119 */     this.locEye[1][1] = this.hFattr[106];
/* 120 */     return this.locEye;
/*     */   }
/*     */ 
/*     */   public void setLocEye(int l0, int l1, int r0, int r1) {
/* 124 */     this.hFattr[103] = l0;
/* 125 */     this.hFattr[104] = l1;
/* 126 */     this.hFattr[105] = r0;
/* 127 */     this.hFattr[106] = r1;
/*     */   }
/*     */ 
/*     */   public int getEyeDegree() {
/* 131 */     this.eyeDegree = this.hFattr[107];
/* 132 */     return this.eyeDegree;
/*     */   }
/*     */ 
/*     */   public void setEyeDegree(int eyeDegree) {
/* 136 */     this.hFattr[107] = eyeDegree;
/*     */   }
/*     */ 
/*     */   public int getMouthDegree() {
/* 140 */     this.mouthDegree = this.hFattr[108];
/* 141 */     return this.mouthDegree;
/*     */   }
/*     */ 
/*     */   public void setMouthDegree(int mouthDegree) {
/* 145 */     this.hFattr[108] = mouthDegree;
/*     */   }
/*     */ 
/*     */   public int[] getHeadPosi() {
/* 149 */     this.headPosi[0] = this.hFattr[109];
/* 150 */     this.headPosi[1] = this.hFattr[110];
/* 151 */     this.headPosi[2] = this.hFattr[111];
/* 152 */     return this.headPosi;
/*     */   }
/*     */ 
/*     */   public void setHeadPosi(int shake, int turn, int up)
/*     */   {
/* 165 */     this.headPosi[0] = shake;
/* 166 */     this.headPosi[1] = turn;
/* 167 */     this.headPosi[2] = up;
/*     */ 
/* 169 */     this.hFattr[109] = this.headPosi[0];
/* 170 */     this.hFattr[110] = this.headPosi[1];
/* 171 */     this.hFattr[111] = this.headPosi[2];
/*     */   }
/*     */ 
/*     */   public int getAge() {
/* 175 */     this.age = this.hFattr[112];
/* 176 */     return this.age;
/*     */   }
/*     */ 
/*     */   public void setAge(int age) {
/* 180 */     this.hFattr[112] = age;
/*     */   }
/*     */ 
/*     */   public int getGender() {
/* 184 */     this.gender = this.hFattr[113];
/* 185 */     return this.gender;
/*     */   }
/*     */ 
/*     */   public void setGender(int gender) {
/* 189 */     this.hFattr[113] = gender;
/*     */   }
/*     */ 
/*     */   public int getOcclusion() {
/* 193 */     this.occlusion = this.hFattr[114];
/* 194 */     return this.occlusion;
/*     */   }
/*     */ 
/*     */   public void setOcclusion(int occlusion) {
/* 198 */     this.hFattr[114] = occlusion;
/*     */   }
/*     */ }

/* Location:           G:\安卓人脸单机版（比对）-timeout_20170701-SDK20170317_v1.2.4\FaceSDK_SingleVersion_Android_Release_V1.2.3_update_20170317\demo\FaceRecognizeDemo-time\libs\tesofacelib-time.jar
 * Qualified Name:     com.smartshino.face.Stfaceattr
 * JD-Core Version:    0.5.4
 */