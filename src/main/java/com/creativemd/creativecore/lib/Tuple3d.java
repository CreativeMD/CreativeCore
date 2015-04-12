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
/*     */ public abstract class Tuple3d
/*     */   implements Serializable, Cloneable
/*     */ {
/*     */   static final long serialVersionUID = 5542096614926168415L;
/*     */   public double x;
/*     */   public double y;
/*     */   public double z;
/*     */   
/*     */   public Tuple3d(double paramDouble1, double paramDouble2, double paramDouble3)
/*     */   {
/*  50 */     this.x = paramDouble1;
/*  51 */     this.y = paramDouble2;
/*  52 */     this.z = paramDouble3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tuple3d(double[] paramArrayOfDouble)
/*     */   {
/*  61 */     this.x = paramArrayOfDouble[0];
/*  62 */     this.y = paramArrayOfDouble[1];
/*  63 */     this.z = paramArrayOfDouble[2];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tuple3d(Tuple3d paramTuple3d)
/*     */   {
/*  72 */     this.x = paramTuple3d.x;
/*  73 */     this.y = paramTuple3d.y;
/*  74 */     this.z = paramTuple3d.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public Tuple3d()
/*     */   {
/*  93 */     this.x = 0.0D;
/*  94 */     this.y = 0.0D;
/*  95 */     this.z = 0.0D;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void set(double paramDouble1, double paramDouble2, double paramDouble3)
/*     */   {
/* 106 */     this.x = paramDouble1;
/* 107 */     this.y = paramDouble2;
/* 108 */     this.z = paramDouble3;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void set(double[] paramArrayOfDouble)
/*     */   {
/* 118 */     this.x = paramArrayOfDouble[0];
/* 119 */     this.y = paramArrayOfDouble[1];
/* 120 */     this.z = paramArrayOfDouble[2];
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void set(Tuple3d paramTuple3d)
/*     */   {
/* 129 */     this.x = paramTuple3d.x;
/* 130 */     this.y = paramTuple3d.y;
/* 131 */     this.z = paramTuple3d.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void get(double[] paramArrayOfDouble)
/*     */   {
/* 152 */     paramArrayOfDouble[0] = this.x;
/* 153 */     paramArrayOfDouble[1] = this.y;
/* 154 */     paramArrayOfDouble[2] = this.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void get(Tuple3d paramTuple3d)
/*     */   {
/* 164 */     paramTuple3d.x = this.x;
/* 165 */     paramTuple3d.y = this.y;
/* 166 */     paramTuple3d.z = this.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void add(Tuple3d paramTuple3d1, Tuple3d paramTuple3d2)
/*     */   {
/* 177 */     paramTuple3d1.x += paramTuple3d2.x;
/* 178 */     paramTuple3d1.y += paramTuple3d2.y;
/* 179 */     paramTuple3d1.z += paramTuple3d2.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void add(Tuple3d paramTuple3d)
/*     */   {
/* 189 */     this.x += paramTuple3d.x;
/* 190 */     this.y += paramTuple3d.y;
/* 191 */     this.z += paramTuple3d.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void sub(Tuple3d paramTuple3d1, Tuple3d paramTuple3d2)
/*     */   {
/* 202 */     paramTuple3d1.x -= paramTuple3d2.x;
/* 203 */     paramTuple3d1.y -= paramTuple3d2.y;
/* 204 */     paramTuple3d1.z -= paramTuple3d2.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void sub(Tuple3d paramTuple3d)
/*     */   {
/* 214 */     this.x -= paramTuple3d.x;
/* 215 */     this.y -= paramTuple3d.y;
/* 216 */     this.z -= paramTuple3d.z;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void negate(Tuple3d paramTuple3d)
/*     */   {
/* 226 */     this.x = (-paramTuple3d.x);
/* 227 */     this.y = (-paramTuple3d.y);
/* 228 */     this.z = (-paramTuple3d.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void negate()
/*     */   {
/* 237 */     this.x = (-this.x);
/* 238 */     this.y = (-this.y);
/* 239 */     this.z = (-this.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void scale(double paramDouble, Tuple3d paramTuple3d)
/*     */   {
/* 251 */     this.x = (paramDouble * paramTuple3d.x);
/* 252 */     this.y = (paramDouble * paramTuple3d.y);
/* 253 */     this.z = (paramDouble * paramTuple3d.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void scale(double paramDouble)
/*     */   {
/* 264 */     this.x *= paramDouble;
/* 265 */     this.y *= paramDouble;
/* 266 */     this.z *= paramDouble;
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
/*     */   public final void scaleAdd(double paramDouble, Tuple3d paramTuple3d1, Tuple3d paramTuple3d2)
/*     */   {
/* 279 */     this.x = (paramDouble * paramTuple3d1.x + paramTuple3d2.x);
/* 280 */     this.y = (paramDouble * paramTuple3d1.y + paramTuple3d2.y);
/* 281 */     this.z = (paramDouble * paramTuple3d1.z + paramTuple3d2.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void scaleAdd(double paramDouble, Tuple3d paramTuple3d)
/*     */   {
/* 300 */     this.x = (paramDouble * this.x + paramTuple3d.x);
/* 301 */     this.y = (paramDouble * this.y + paramTuple3d.y);
/* 302 */     this.z = (paramDouble * this.z + paramTuple3d.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public String toString()
/*     */   {
/* 313 */     return "(" + this.x + ", " + this.y + ", " + this.z + ")";
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
/*     */   public int hashCode()
/*     */   {
/* 326 */     long l = 1L;
/* 327 */     l = 31L * l + Double.doubleToLongBits(this.x);
/* 328 */     l = 31L * l + Double.doubleToLongBits(this.y);
/* 329 */     l = 31L * l + Double.doubleToLongBits(this.z);
/* 330 */     return (int)(l ^ l >> 32);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Tuple3d paramTuple3d)
/*     */   {
/*     */     try
/*     */     {
/* 343 */       return (this.x == paramTuple3d.x) && (this.y == paramTuple3d.y) && (this.z == paramTuple3d.z);
/*     */     } catch (NullPointerException localNullPointerException) {}
/* 345 */     return false;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean equals(Object paramObject)
/*     */   {
/*     */     try
/*     */     {
/* 358 */       Tuple3d localTuple3d = (Tuple3d)paramObject;
/* 359 */       return (this.x == localTuple3d.x) && (this.y == localTuple3d.y) && (this.z == localTuple3d.z);
/*     */     } catch (ClassCastException localClassCastException) {
/* 361 */       return false; } catch (NullPointerException localNullPointerException) {}
/* 362 */     return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public boolean epsilonEquals(Tuple3d paramTuple3d, double paramDouble)
/*     */   {
/* 379 */     double d = this.x - paramTuple3d.x;
/* 380 */     if ((d < 0.0D ? -d : d) > paramDouble) { return false;
/*     */     }
/* 382 */     d = this.y - paramTuple3d.y;
/* 383 */     if ((d < 0.0D ? -d : d) > paramDouble) { return false;
/*     */     }
/* 385 */     d = this.z - paramTuple3d.z;
/* 386 */     if ((d < 0.0D ? -d : d) > paramDouble) { return false;
/*     */     }
/* 388 */     return true;
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void clamp(double paramDouble1, double paramDouble2, Tuple3d paramTuple3d)
/*     */   {
/* 409 */     if (paramTuple3d.x > paramDouble2) {
/* 410 */       this.x = paramDouble2;
/* 411 */     } else if (paramTuple3d.x < paramDouble1) {
/* 412 */       this.x = paramDouble1;
/*     */     } else {
/* 414 */       this.x = paramTuple3d.x;
/*     */     }
/*     */     
/* 417 */     if (paramTuple3d.y > paramDouble2) {
/* 418 */       this.y = paramDouble2;
/* 419 */     } else if (paramTuple3d.y < paramDouble1) {
/* 420 */       this.y = paramDouble1;
/*     */     } else {
/* 422 */       this.y = paramTuple3d.y;
/*     */     }
/*     */     
/* 425 */     if (paramTuple3d.z > paramDouble2) {
/* 426 */       this.z = paramDouble2;
/* 427 */     } else if (paramTuple3d.z < paramDouble1) {
/* 428 */       this.z = paramDouble1;
/*     */     } else {
/* 430 */       this.z = paramTuple3d.z;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void clampMin(double paramDouble, Tuple3d paramTuple3d)
/*     */   {
/* 451 */     if (paramTuple3d.x < paramDouble) {
/* 452 */       this.x = paramDouble;
/*     */     } else {
/* 454 */       this.x = paramTuple3d.x;
/*     */     }
/*     */     
/* 457 */     if (paramTuple3d.y < paramDouble) {
/* 458 */       this.y = paramDouble;
/*     */     } else {
/* 460 */       this.y = paramTuple3d.y;
/*     */     }
/*     */     
/* 463 */     if (paramTuple3d.z < paramDouble) {
/* 464 */       this.z = paramDouble;
/*     */     } else {
/* 466 */       this.z = paramTuple3d.z;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void clampMax(double paramDouble, Tuple3d paramTuple3d)
/*     */   {
/* 487 */     if (paramTuple3d.x > paramDouble) {
/* 488 */       this.x = paramDouble;
/*     */     } else {
/* 490 */       this.x = paramTuple3d.x;
/*     */     }
/*     */     
/* 493 */     if (paramTuple3d.y > paramDouble) {
/* 494 */       this.y = paramDouble;
/*     */     } else {
/* 496 */       this.y = paramTuple3d.y;
/*     */     }
/*     */     
/* 499 */     if (paramTuple3d.z > paramDouble) {
/* 500 */       this.z = paramDouble;
/*     */     } else {
/* 502 */       this.z = paramTuple3d.z;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void absolute(Tuple3d paramTuple3d)
/*     */   {
/* 515 */     this.x = Math.abs(paramTuple3d.x);
/* 516 */     this.y = Math.abs(paramTuple3d.y);
/* 517 */     this.z = Math.abs(paramTuple3d.z);
/*     */   }
/*     */   
/*     */
/*     */ 
/*     */ 
/*     */   public final void clamp(double paramDouble1, double paramDouble2)
/*     */   {
/* 536 */     if (this.x > paramDouble2) {
/* 537 */       this.x = paramDouble2;
/* 538 */     } else if (this.x < paramDouble1) {
/* 539 */       this.x = paramDouble1;
/*     */     }
/*     */     
/* 542 */     if (this.y > paramDouble2) {
/* 543 */       this.y = paramDouble2;
/* 544 */     } else if (this.y < paramDouble1) {
/* 545 */       this.y = paramDouble1;
/*     */     }
/*     */     
/* 548 */     if (this.z > paramDouble2) {
/* 549 */       this.z = paramDouble2;
/* 550 */     } else if (this.z < paramDouble1) {
/* 551 */       this.z = paramDouble1;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void clampMin(double paramDouble)
/*     */   {
/* 570 */     if (this.x < paramDouble) this.x = paramDouble;
/* 571 */     if (this.y < paramDouble) this.y = paramDouble;
/* 572 */     if (this.z < paramDouble) { this.z = paramDouble;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */   public final void clampMax(double paramDouble)
/*     */   {
/* 590 */     if (this.x > paramDouble) this.x = paramDouble;
/* 591 */     if (this.y > paramDouble) this.y = paramDouble;
/* 592 */     if (this.z > paramDouble) { this.z = paramDouble;
/*     */     }
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void absolute()
/*     */   {
/* 601 */     this.x = Math.abs(this.x);
/* 602 */     this.y = Math.abs(this.y);
/* 603 */     this.z = Math.abs(this.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public final void interpolate(Tuple3d paramTuple3d1, Tuple3d paramTuple3d2, double paramDouble)
/*     */   {
/* 623 */     this.x = ((1.0D - paramDouble) * paramTuple3d1.x + paramDouble * paramTuple3d2.x);
/* 624 */     this.y = ((1.0D - paramDouble) * paramTuple3d1.y + paramDouble * paramTuple3d2.y);
/* 625 */     this.z = ((1.0D - paramDouble) * paramTuple3d1.z + paramDouble * paramTuple3d2.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */   public final void interpolate(Tuple3d paramTuple3d, double paramDouble)
/*     */   {
/* 644 */     this.x = ((1.0D - paramDouble) * this.x + paramDouble * paramTuple3d.x);
/* 645 */     this.y = ((1.0D - paramDouble) * this.y + paramDouble * paramTuple3d.y);
/* 646 */     this.z = ((1.0D - paramDouble) * this.z + paramDouble * paramTuple3d.z);
/*     */   }
/*     */   
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/* 660 */       return super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException localCloneNotSupportedException) {
/* 663 */       throw new InternalError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\CMD\.gradle\caches\modules-2\files-2.1\java3d\vecmath\1.3.1\a0ae4f51da409fa0c20fa0ca59e6bbc9413ae71d\vecmath-1.3.1.jar
 * Qualified Name:     javax.vecmath.Tuple3d
 * Java Class Version: 1.2 (46.0)
 * JD-Core Version:    0.7.0.1
 */