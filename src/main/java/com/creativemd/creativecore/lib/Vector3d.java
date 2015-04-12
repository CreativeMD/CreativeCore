/*     */ package com.creativemd.creativecore.lib;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Vector3d
/*     */   extends Tuple3d
/*     */   implements Serializable
/*     */ {
/*     */   static final long serialVersionUID = 3761969948420550442L;
/*     */   
/*     */   public Vector3d(double paramDouble1, double paramDouble2, double paramDouble3)
/*     */   {
/*  36 */     super(paramDouble1, paramDouble2, paramDouble3);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector3d(double[] paramArrayOfDouble)
/*     */   {
/*  46 */     super(paramArrayOfDouble);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector3d(Vector3d paramVector3d)
/*     */   {
/*  56 */     super(paramVector3d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector3d(Tuple3d paramTuple3d)
/*     */   {
/*  86 */     super(paramTuple3d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Vector3d() {}
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void cross(Vector3d paramVector3d1, Vector3d paramVector3d2)
/*     */   {
/* 108 */     double d1 = paramVector3d1.y * paramVector3d2.z - paramVector3d1.z * paramVector3d2.y;
/* 109 */     double d2 = paramVector3d2.x * paramVector3d1.z - paramVector3d2.z * paramVector3d1.x;
/* 110 */     this.z = (paramVector3d1.x * paramVector3d2.y - paramVector3d1.y * paramVector3d2.x);
/* 111 */     this.x = d1;
/* 112 */     this.y = d2;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void normalize(Vector3d paramVector3d)
/*     */   {
/* 124 */     double d = 1.0D / Math.sqrt(paramVector3d.x * paramVector3d.x + paramVector3d.y * paramVector3d.y + paramVector3d.z * paramVector3d.z);
/* 125 */     this.x = (paramVector3d.x * d);
/* 126 */     this.y = (paramVector3d.y * d);
/* 127 */     this.z = (paramVector3d.z * d);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void normalize()
/*     */   {
/* 138 */     double d = 1.0D / Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
/* 139 */     this.x *= d;
/* 140 */     this.y *= d;
/* 141 */     this.z *= d;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final double dot(Vector3d paramVector3d)
/*     */   {
/* 152 */     return this.x * paramVector3d.x + this.y * paramVector3d.y + this.z * paramVector3d.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final double lengthSquared()
/*     */   {
/* 162 */     return this.x * this.x + this.y * this.y + this.z * this.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final double length()
/*     */   {
/* 172 */     return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final double angle(Vector3d paramVector3d)
/*     */   {
/* 184 */     double d = dot(paramVector3d) / (length() * paramVector3d.length());
/* 185 */     if (d < -1.0D) d = -1.0D;
/* 186 */     if (d > 1.0D) d = 1.0D;
/* 187 */     return Math.acos(d);
/*     */   }
/*     */ }

/* Location:           C:\Users\CMD\.gradle\caches\modules-2\files-2.1\java3d\vecmath\1.3.1\a0ae4f51da409fa0c20fa0ca59e6bbc9413ae71d\vecmath-1.3.1.jar
 * Qualified Name:     javax.vecmath.Vector3d
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.0.1
 */