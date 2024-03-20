package team.creative.creativecore.common.util.math.matrix;

import team.creative.creativecore.common.util.math.vec.Vec3d;
import team.creative.creativecore.common.util.math.vec.Vec3f;

public class Matrix3 {
    
    public double m00;
    public double m01;
    public double m02;
    
    public double m10;
    public double m11;
    public double m12;
    
    public double m20;
    public double m21;
    public double m22;
    
    private static final double EPS = 1.110223024E-16;
    
    public Matrix3() {
        this(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
    
    public Matrix3(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        
    }
    
    public Matrix3(Matrix3 matrix) {
        set(matrix);
    }
    
    @Override
    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }
    
    public final void setIdentity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }
    
    /** Sets the scale component of the current matrix by factoring
     * out the current scale (by doing an SVD) and multiplying by
     * the new scale.
     * 
     * @param scale
     *            the new scale amount */
    public final void setScale(double scale) {
        
        double[] tmp_rot = new double[9]; // scratch matrix
        double[] tmp_scale = new double[3]; // scratch matrix
        
        getScaleRotate(tmp_scale, tmp_rot);
        
        this.m00 = tmp_rot[0] * scale;
        this.m01 = tmp_rot[1] * scale;
        this.m02 = tmp_rot[2] * scale;
        
        this.m10 = tmp_rot[3] * scale;
        this.m11 = tmp_rot[4] * scale;
        this.m12 = tmp_rot[5] * scale;
        
        this.m20 = tmp_rot[6] * scale;
        this.m21 = tmp_rot[7] * scale;
        this.m22 = tmp_rot[8] * scale;
    }
    
    /** Sets the specified element of this matrix3f to the value provided.
     * 
     * @param row
     *            the row number to be modified (zero indexed)
     * @param column
     *            the column number to be modified (zero indexed)
     * @param value
     *            the new value */
    public final void setElement(int row, int column, double value) {
        switch (row) {
            case 0:
                switch (column) {
                    case 0:
                        this.m00 = value;
                        break;
                    case 1:
                        this.m01 = value;
                        break;
                    case 2:
                        this.m02 = value;
                        break;
                    default:
                        throw new ArrayIndexOutOfBoundsException();
                }
                break;
            
            case 1:
                switch (column) {
                    case 0:
                        this.m10 = value;
                        break;
                    case 1:
                        this.m11 = value;
                        break;
                    case 2:
                        this.m12 = value;
                        break;
                    default:
                        throw new ArrayIndexOutOfBoundsException();
                }
                break;
            
            case 2:
                switch (column) {
                    case 0:
                        this.m20 = value;
                        break;
                    case 1:
                        this.m21 = value;
                        break;
                    case 2:
                        this.m22 = value;
                        break;
                    default:
                        throw new ArrayIndexOutOfBoundsException();
                }
                break;
            
            default:
                throw new ArrayIndexOutOfBoundsException();
        }
    }
    
    /** Retrieves the value at the specified row and column of the specified
     * matrix.
     * 
     * @param row
     *            the row number to be retrieved (zero indexed)
     * @param column
     *            the column number to be retrieved (zero indexed)
     * @return the value at the indexed element. */
    public final double getElement(int row, int column) {
        switch (row) {
            case 0:
                switch (column) {
                    case 0:
                        return (this.m00);
                    case 1:
                        return (this.m01);
                    case 2:
                        return (this.m02);
                    default:
                        break;
                }
                break;
            case 1:
                switch (column) {
                    case 0:
                        return (this.m10);
                    case 1:
                        return (this.m11);
                    case 2:
                        return (this.m12);
                    default:
                        break;
                }
                break;
            
            case 2:
                switch (column) {
                    case 0:
                        return (this.m20);
                    case 1:
                        return (this.m21);
                    case 2:
                        return (this.m22);
                    default:
                        break;
                }
                break;
            
            default:
                break;
        }
        
        throw new ArrayIndexOutOfBoundsException();
    }
    
    /** Performs an SVD normalization of this matrix to calculate
     * and return the uniform scale factor. If the matrix has non-uniform
     * scale factors, the largest of the x, y, and z scale factors will
     * be returned. This matrix is not modified.
     * 
     * @return the scale factor of this matrix */
    public final double getScale() {
        
        double[] tmp_scale = new double[3]; // scratch matrix
        double[] tmp_rot = new double[9]; // scratch matrix
        getScaleRotate(tmp_scale, tmp_rot);
        
        return (max3(tmp_scale));
        
    }
    
    public final void add(double scalar) {
        m00 += scalar;
        m01 += scalar;
        m02 += scalar;
        
        m10 += scalar;
        m11 += scalar;
        m12 += scalar;
        
        m20 += scalar;
        m21 += scalar;
        m22 += scalar;
        
    }
    
    public final void add(Matrix3 m1) {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        
        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        
        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
    }
    
    public final void sub(Matrix3 m1) {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        
        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        
        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
    }
    
    public final void transpose() {
        double temp;
        
        temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }
    
    public final void set(Matrix3 m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }
    
    public final void invert() {
        double[] result = new double[9];
        int[] row_perm = new int[3];
        int i;
        double[] tmp = new double[9]; // scratch matrix
        
        // Use LU decomposition and backsubstitution code specifically
        // for floating-point 3x3 matrices.
        
        // Copy source matrix to t1tmp
        tmp[0] = m00;
        tmp[1] = m01;
        tmp[2] = m02;
        
        tmp[3] = m10;
        tmp[4] = m11;
        tmp[5] = m12;
        
        tmp[6] = m20;
        tmp[7] = m21;
        tmp[8] = m22;
        
        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(tmp, row_perm)) {
            // Matrix has no inverse
            throw new RuntimeException("Cannot be reverted");
        }
        
        // Perform back substitution on the identity matrix
        for (i = 0; i < 9; i++)
            result[i] = 0.0;
        result[0] = 1.0;
        result[4] = 1.0;
        result[8] = 1.0;
        luBacksubstitution(tmp, row_perm, result);
        
        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        
        this.m10 = result[3];
        this.m11 = result[4];
        this.m12 = result[5];
        
        this.m20 = result[6];
        this.m21 = result[7];
        this.m22 = result[8];
        
    }
    
    public final void invert(Matrix3 m) {
        double[] result = new double[9];
        int[] row_perm = new int[3];
        int i;
        double[] tmp = new double[9]; // scratch matrix
        
        // Use LU decomposition and backsubstitution code specifically
        // for floating-point 3x3 matrices.
        
        // Copy source matrix to t1tmp
        tmp[0] = m.m00;
        tmp[1] = m.m01;
        tmp[2] = m.m02;
        
        tmp[3] = m.m10;
        tmp[4] = m.m11;
        tmp[5] = m.m12;
        
        tmp[6] = m.m20;
        tmp[7] = m.m21;
        tmp[8] = m.m22;
        
        // Calculate LU decomposition: Is the matrix singular?
        if (!luDecomposition(tmp, row_perm)) {
            // Matrix has no inverse
            throw new RuntimeException("Cannot be reverted");
        }
        
        // Perform back substitution on the identity matrix
        for (i = 0; i < 9; i++)
            result[i] = 0.0;
        result[0] = 1.0;
        result[4] = 1.0;
        result[8] = 1.0;
        luBacksubstitution(tmp, row_perm, result);
        
        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        
        this.m10 = result[3];
        this.m11 = result[4];
        this.m12 = result[5];
        
        this.m20 = result[6];
        this.m21 = result[7];
        this.m22 = result[8];
        
    }
    
    /** Given a 3x3 array "matrix0", this function replaces it with the
     * LU decomposition of a row-wise permutation of itself. The input
     * parameters are "matrix0" and "dimen". The array "matrix0" is also
     * an output parameter. The vector "row_perm[3]" is an output
     * parameter that contains the row permutations resulting from partial
     * pivoting. The output parameter "even_row_xchg" is 1 when the
     * number of row exchanges is even, or -1 otherwise. Assumes data
     * type is always double.
     *
     * This function is similar to luDecomposition, except that it
     * is tuned specifically for 3x3 matrices.
     *
     * @return true if the matrix is nonsingular, or false otherwise. */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 40-45.
    //
    static boolean luDecomposition(double[] matrix0, int[] row_perm) {
        
        double[] row_scale = new double[3];
        
        // Determine implicit scaling information by looping over rows
        {
            int i, j;
            int ptr, rs;
            double big, temp;
            
            ptr = 0;
            rs = 0;
            
            // For each row ...
            i = 3;
            while (i-- != 0) {
                big = 0.0;
                
                // For each column, find the largest element in the row
                j = 3;
                while (j-- != 0) {
                    temp = matrix0[ptr++];
                    temp = Math.abs(temp);
                    if (temp > big) {
                        big = temp;
                    }
                }
                
                // Is the matrix singular?
                if (big == 0.0) {
                    return false;
                }
                row_scale[rs++] = 1.0 / big;
            }
        }
        
        {
            int j;
            int mtx;
            
            mtx = 0;
            
            // For all columns, execute Crout's method
            for (j = 0; j < 3; j++) {
                int i, imax, k;
                int target, p1, p2;
                double sum, big, temp;
                
                // Determine elements of upper diagonal matrix U
                for (i = 0; i < j; i++) {
                    target = mtx + (3 * i) + j;
                    sum = matrix0[target];
                    k = i;
                    p1 = mtx + (3 * i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 3;
                    }
                    matrix0[target] = sum;
                }
                
                // Search for largest pivot element and calculate
                // intermediate elements of lower diagonal matrix L.
                big = 0.0;
                imax = -1;
                for (i = j; i < 3; i++) {
                    target = mtx + (3 * i) + j;
                    sum = matrix0[target];
                    k = j;
                    p1 = mtx + (3 * i);
                    p2 = mtx + j;
                    while (k-- != 0) {
                        sum -= matrix0[p1] * matrix0[p2];
                        p1++;
                        p2 += 3;
                    }
                    matrix0[target] = sum;
                    
                    // Is this the best pivot so far?
                    if ((temp = row_scale[i] * Math.abs(sum)) >= big) {
                        big = temp;
                        imax = i;
                    }
                }
                
                if (imax < 0) {
                    throw new RuntimeException();
                }
                
                // Is a row exchange necessary?
                if (j != imax) {
                    // Yes: exchange rows
                    k = 3;
                    p1 = mtx + (3 * imax);
                    p2 = mtx + (3 * j);
                    while (k-- != 0) {
                        temp = matrix0[p1];
                        matrix0[p1++] = matrix0[p2];
                        matrix0[p2++] = temp;
                    }
                    
                    // Record change in scale factor
                    row_scale[imax] = row_scale[j];
                }
                
                // Record row permutation
                row_perm[j] = imax;
                
                // Is the matrix singular
                if (matrix0[(mtx + (3 * j) + j)] == 0.0) {
                    return false;
                }
                
                // Divide elements of lower diagonal matrix L by pivot
                if (j != (3 - 1)) {
                    temp = 1.0 / (matrix0[(mtx + (3 * j) + j)]);
                    target = mtx + (3 * (j + 1)) + j;
                    i = 2 - j;
                    while (i-- != 0) {
                        matrix0[target] *= temp;
                        target += 3;
                    }
                }
            }
        }
        
        return true;
    }
    
    /** Solves a set of linear equations. The input parameters "matrix1",
     * and "row_perm" come from luDecompostionD3x3 and do not change
     * here. The parameter "matrix2" is a set of column vectors assembled
     * into a 3x3 matrix of floating-point values. The procedure takes each
     * column of "matrix2" in turn and treats it as the right-hand side of the
     * matrix equation Ax = LUx = b. The solution vector replaces the
     * original column of the matrix.
     *
     * If "matrix2" is the identity matrix, the procedure replaces its contents
     * with the inverse of the matrix from which "matrix1" was originally
     * derived. */
    //
    // Reference: Press, Flannery, Teukolsky, Vetterling,
    //	      _Numerical_Recipes_in_C_, Cambridge University Press,
    //	      1988, pp 44-45.
    //
    static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2) {
        
        int i, ii, ip, j, k;
        int rp;
        int cv, rv;
        
        //	rp = row_perm;
        rp = 0;
        
        // For each column vector of matrix2 ...
        for (k = 0; k < 3; k++) {
            //	    cv = &(matrix2[0][k]);
            cv = k;
            ii = -1;
            
            // Forward substitution
            for (i = 0; i < 3; i++) {
                double sum;
                
                ip = row_perm[rp + i];
                sum = matrix2[cv + 3 * ip];
                matrix2[cv + 3 * ip] = matrix2[cv + 3 * i];
                if (ii >= 0) {
                    //		    rv = &(matrix1[i][0]);
                    rv = i * 3;
                    for (j = ii; j <= i - 1; j++) {
                        sum -= matrix1[rv + j] * matrix2[cv + 3 * j];
                    }
                } else if (sum != 0.0) {
                    ii = i;
                }
                matrix2[cv + 3 * i] = sum;
            }
            
            // Backsubstitution
            //	    rv = &(matrix1[3][0]);
            rv = 2 * 3;
            matrix2[cv + 3 * 2] /= matrix1[rv + 2];
            
            rv -= 3;
            matrix2[cv + 3] = (matrix2[cv + 3] - matrix1[rv + 2] * matrix2[cv + 3 * 2]) / matrix1[rv + 1];
            
            rv -= 3;
            matrix2[cv] = (matrix2[cv] - matrix1[rv + 1] * matrix2[cv + 3] - matrix1[rv + 2] * matrix2[cv + 3 * 2]) / matrix1[rv];
            
        }
    }
    
    public final double determinant() {
        double total;
        
        total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }
    
    public final void set(double scale) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
    }
    
    public final void rotX(double angle) {
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        
        this.m10 = 0.0;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        
        this.m20 = 0.0;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
    }
    
    public final void rotY(double angle) {
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        
        this.m00 = cosAngle;
        this.m01 = 0.0;
        this.m02 = sinAngle;
        
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        
        this.m20 = -sinAngle;
        this.m21 = 0.0;
        this.m22 = cosAngle;
    }
    
    public final void rotZ(double angle) {
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        
        this.m00 = cosAngle;
        this.m01 = -sinAngle;
        this.m02 = 0.0;
        
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0;
        
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }
    
    public final void mul(double scalar) {
        m00 *= scalar;
        m01 *= scalar;
        m02 *= scalar;
        
        m10 *= scalar;
        m11 *= scalar;
        m12 *= scalar;
        
        m20 *= scalar;
        m21 *= scalar;
        m22 *= scalar;
        
    }
    
    public final void mul(Matrix3 m1) {
        double m00 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        double m01 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        double m02 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        
        double m10 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        double m11 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        double m12 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        
        double m20 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        double m21 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        double m22 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }
    
    /** Multiplies this matrix by matrix m1, does an SVD normalization
     * of the result, and places the result back into this matrix
     * this = SVDnorm(this*m1).
     * 
     * @param m1
     *            the matrix on the right hand side of the multiplication */
    public final void mulNormalize(Matrix3 m1) {
        
        double[] tmp = new double[9]; // scratch matrix
        double[] tmp_rot = new double[9]; // scratch matrix
        double[] tmp_scale = new double[3]; // scratch matrix
        
        tmp[0] = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        tmp[1] = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        tmp[2] = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        
        tmp[3] = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        tmp[4] = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        tmp[5] = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        
        tmp[6] = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        tmp[7] = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        tmp[8] = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        
        compute_svd(tmp, tmp_scale, tmp_rot);
        
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
        
    }
    
    public final void normalize() {
        double[] tmp_rot = new double[9]; // scratch matrix
        double[] tmp_scale = new double[3]; // scratch matrix
        
        getScaleRotate(tmp_scale, tmp_rot);
        
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
        
    }
    
    public final void normalizeCP() {
        double mag = 1.0 / Math.sqrt(m00 * m00 + m10 * m10 + m20 * m20);
        m00 = m00 * mag;
        m10 = m10 * mag;
        m20 = m20 * mag;
        
        mag = 1.0 / Math.sqrt(m01 * m01 + m11 * m11 + m21 * m21);
        m01 = m01 * mag;
        m11 = m11 * mag;
        m21 = m21 * mag;
        
        m02 = m10 * m21 - m11 * m20;
        m12 = m01 * m20 - m00 * m21;
        m22 = m00 * m11 - m01 * m10;
    }
    
    /** Returns true if all of the data members of Matrix3d m1 are
     * equal to the corresponding data members in this Matrix3d.
     * 
     * @param m1
     *            the matrix with which the comparison is made
     * @return true or false */
    public boolean equals(Matrix3 m1) {
        try {
            return (this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22);
        } catch (NullPointerException e2) {
            return false;
        }
        
    }
    
    /** Returns true if the Object t1 is of type Matrix3d and all of the
     * data members of t1 are equal to the corresponding data members in
     * this Matrix3d.
     * 
     * @param t1
     *            the matrix with which the comparison is made
     * @return true or false */
    @Override
    public boolean equals(Object t1) {
        try {
            Matrix3 m2 = (Matrix3) t1;
            return (this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22);
        } catch (ClassCastException | NullPointerException e1) {
            return false;
        }
        
    }
    
    /** Multiply this matrix by the tuple t and place the result
     * back into the tuple (t = this*t).
     * 
     * @param t
     *            the tuple to be multiplied by this matrix and then replaced */
    public final void transform(Vec3d t) {
        double x = m00 * t.x + m01 * t.y + m02 * t.z;
        double y = m10 * t.x + m11 * t.y + m12 * t.z;
        double z = m20 * t.x + m21 * t.y + m22 * t.z;
        t.set(x, y, z);
    }
    
    /** Multiply this matrix by the tuple t and place the result
     * back into the tuple (t = this*t).
     * 
     * @param t
     *            the tuple to be multiplied by this matrix and then replaced */
    public final void transform(Vec3f t) {
        float x = (float) (m00 * t.x + m01 * t.y + m02 * t.z);
        float y = (float) (m10 * t.x + m11 * t.y + m12 * t.z);
        float z = (float) (m20 * t.x + m21 * t.y + m22 * t.z);
        t.set(x, y, z);
    }
    
    /** perform SVD (if necessary to get rotational component */
    final void getScaleRotate(double[] scales, double[] rots) {
        
        double[] tmp = new double[9]; // scratch matrix
        
        tmp[0] = m00;
        tmp[1] = m01;
        tmp[2] = m02;
        
        tmp[3] = m10;
        tmp[4] = m11;
        tmp[5] = m12;
        
        tmp[6] = m20;
        tmp[7] = m21;
        tmp[8] = m22;
        compute_svd(tmp, scales, rots);
        
    }
    
    static void compute_svd(double[] m, double[] outScale, double[] outRot) {
        int i;
        double g;
        double[] u1 = new double[9];
        double[] v1 = new double[9];
        double[] t1 = new double[9];
        double[] t2 = new double[9];
        
        double[] tmp = t1;
        double[] single_values = t2;
        
        double[] rot = new double[9];
        double[] e = new double[3];
        double[] scales = new double[3];
        
        int negCnt = 0;
        double c1, c2, c3, c4;
        double s1, s2, s3, s4;
        
        for (i = 0; i < 9; i++)
            rot[i] = m[i];
        
        // u1
        
        if (m[3] * m[3] < EPS) {
            u1[0] = 1.0;
            u1[1] = 0.0;
            u1[2] = 0.0;
            u1[3] = 0.0;
            u1[4] = 1.0;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        } else if (m[0] * m[0] < EPS) {
            tmp[0] = m[0];
            tmp[1] = m[1];
            tmp[2] = m[2];
            m[0] = m[3];
            m[1] = m[4];
            m[2] = m[5];
            
            m[3] = -tmp[0]; // zero
            m[4] = -tmp[1];
            m[5] = -tmp[2];
            
            u1[0] = 0.0;
            u1[1] = 1.0;
            u1[2] = 0.0;
            u1[3] = -1.0;
            u1[4] = 0.0;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        } else {
            g = 1.0 / Math.sqrt(m[0] * m[0] + m[3] * m[3]);
            c1 = m[0] * g;
            s1 = m[3] * g;
            tmp[0] = c1 * m[0] + s1 * m[3];
            tmp[1] = c1 * m[1] + s1 * m[4];
            tmp[2] = c1 * m[2] + s1 * m[5];
            
            m[3] = -s1 * m[0] + c1 * m[3]; // zero
            m[4] = -s1 * m[1] + c1 * m[4];
            m[5] = -s1 * m[2] + c1 * m[5];
            
            m[0] = tmp[0];
            m[1] = tmp[1];
            m[2] = tmp[2];
            u1[0] = c1;
            u1[1] = s1;
            u1[2] = 0.0;
            u1[3] = -s1;
            u1[4] = c1;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        }
        
        // u2
        if (m[6] * m[6] < EPS) {} else if (m[0] * m[0] < EPS) {
            tmp[0] = m[0];
            tmp[1] = m[1];
            tmp[2] = m[2];
            m[0] = m[6];
            m[1] = m[7];
            m[2] = m[8];
            
            m[6] = -tmp[0]; // zero
            m[7] = -tmp[1];
            m[8] = -tmp[2];
            
            tmp[0] = u1[0];
            tmp[1] = u1[1];
            tmp[2] = u1[2];
            u1[0] = u1[6];
            u1[1] = u1[7];
            u1[2] = u1[8];
            
            u1[6] = -tmp[0]; // zero
            u1[7] = -tmp[1];
            u1[8] = -tmp[2];
        } else {
            g = 1.0 / Math.sqrt(m[0] * m[0] + m[6] * m[6]);
            c2 = m[0] * g;
            s2 = m[6] * g;
            tmp[0] = c2 * m[0] + s2 * m[6];
            tmp[1] = c2 * m[1] + s2 * m[7];
            tmp[2] = c2 * m[2] + s2 * m[8];
            
            m[6] = -s2 * m[0] + c2 * m[6];
            m[7] = -s2 * m[1] + c2 * m[7];
            m[8] = -s2 * m[2] + c2 * m[8];
            m[0] = tmp[0];
            m[1] = tmp[1];
            m[2] = tmp[2];
            
            tmp[0] = c2 * u1[0];
            tmp[1] = c2 * u1[1];
            u1[2] = s2;
            
            tmp[6] = -u1[0] * s2;
            tmp[7] = -u1[1] * s2;
            u1[8] = c2;
            u1[0] = tmp[0];
            u1[1] = tmp[1];
            u1[6] = tmp[6];
            u1[7] = tmp[7];
        }
        
        // v1
        
        if (m[2] * m[2] < EPS) {
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = 1.0;
            v1[5] = 0.0;
            v1[6] = 0.0;
            v1[7] = 0.0;
            v1[8] = 1.0;
        } else if (m[1] * m[1] < EPS) {
            tmp[2] = m[2];
            tmp[5] = m[5];
            tmp[8] = m[8];
            m[2] = -m[1];
            m[5] = -m[4];
            m[8] = -m[7];
            
            m[1] = tmp[2]; // zero
            m[4] = tmp[5];
            m[7] = tmp[8];
            
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = 0.0;
            v1[5] = -1.0;
            v1[6] = 0.0;
            v1[7] = 1.0;
            v1[8] = 0.0;
        } else {
            g = 1.0 / Math.sqrt(m[1] * m[1] + m[2] * m[2]);
            c3 = m[1] * g;
            s3 = m[2] * g;
            tmp[1] = c3 * m[1] + s3 * m[2]; // can assign to m[1]?
            m[2] = -s3 * m[1] + c3 * m[2]; // zero
            m[1] = tmp[1];
            
            tmp[4] = c3 * m[4] + s3 * m[5];
            m[5] = -s3 * m[4] + c3 * m[5];
            m[4] = tmp[4];
            
            tmp[7] = c3 * m[7] + s3 * m[8];
            m[8] = -s3 * m[7] + c3 * m[8];
            m[7] = tmp[7];
            
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = c3;
            v1[5] = -s3;
            v1[6] = 0.0;
            v1[7] = s3;
            v1[8] = c3;
        }
        
        // u3
        
        if (m[7] * m[7] < EPS) {} else if (m[4] * m[4] < EPS) {
            tmp[3] = m[3];
            tmp[4] = m[4];
            tmp[5] = m[5];
            m[3] = m[6]; // zero
            m[4] = m[7];
            m[5] = m[8];
            
            m[6] = -tmp[3]; // zero
            m[7] = -tmp[4]; // zero
            m[8] = -tmp[5];
            
            tmp[3] = u1[3];
            tmp[4] = u1[4];
            tmp[5] = u1[5];
            u1[3] = u1[6];
            u1[4] = u1[7];
            u1[5] = u1[8];
            
            u1[6] = -tmp[3]; // zero
            u1[7] = -tmp[4];
            u1[8] = -tmp[5];
            
        } else {
            g = 1.0 / Math.sqrt(m[4] * m[4] + m[7] * m[7]);
            c4 = m[4] * g;
            s4 = m[7] * g;
            tmp[3] = c4 * m[3] + s4 * m[6];
            m[6] = -s4 * m[3] + c4 * m[6]; // zero
            m[3] = tmp[3];
            
            tmp[4] = c4 * m[4] + s4 * m[7];
            m[7] = -s4 * m[4] + c4 * m[7];
            m[4] = tmp[4];
            
            tmp[5] = c4 * m[5] + s4 * m[8];
            m[8] = -s4 * m[5] + c4 * m[8];
            m[5] = tmp[5];
            
            tmp[3] = c4 * u1[3] + s4 * u1[6];
            u1[6] = -s4 * u1[3] + c4 * u1[6];
            u1[3] = tmp[3];
            
            tmp[4] = c4 * u1[4] + s4 * u1[7];
            u1[7] = -s4 * u1[4] + c4 * u1[7];
            u1[4] = tmp[4];
            
            tmp[5] = c4 * u1[5] + s4 * u1[8];
            u1[8] = -s4 * u1[5] + c4 * u1[8];
            u1[5] = tmp[5];
        }
        
        single_values[0] = m[0];
        single_values[1] = m[4];
        single_values[2] = m[8];
        e[0] = m[1];
        e[1] = m[5];
        
        if (e[0] * e[0] < EPS && e[1] * e[1] < EPS) {
            
        } else {
            compute_qr(single_values, e, u1, v1);
        }
        
        scales[0] = single_values[0];
        scales[1] = single_values[1];
        scales[2] = single_values[2];
        
        // Do some optimization here. If scale is unity, simply return the rotation matric.
        if (almostEqual(Math.abs(scales[0]), 1.0) && almostEqual(Math.abs(scales[1]), 1.0) && almostEqual(Math.abs(scales[2]), 1.0)) {
            //  System.out.println("Scale components almost to 1.0");
            
            for (i = 0; i < 3; i++)
                if (scales[i] < 0.0)
                    negCnt++;
                
            if ((negCnt == 0) || (negCnt == 2)) {
                //System.out.println("Optimize!!");
                outScale[0] = outScale[1] = outScale[2] = 1.0;
                for (i = 0; i < 9; i++)
                    outRot[i] = rot[i];
                
                return;
            }
        }
        
        transpose_mat(u1, t1);
        transpose_mat(v1, t2);
        
        /*
          System.out.println("t1 is \n" + t1);
          System.out.println("t1="+t1[0]+" "+t1[1]+" "+t1[2]);
          System.out.println("t1="+t1[3]+" "+t1[4]+" "+t1[5]);
          System.out.println("t1="+t1[6]+" "+t1[7]+" "+t1[8]);
        
          System.out.println("t2 is \n" + t2);
          System.out.println("t2="+t2[0]+" "+t2[1]+" "+t2[2]);
          System.out.println("t2="+t2[3]+" "+t2[4]+" "+t2[5]);
          System.out.println("t2="+t2[6]+" "+t2[7]+" "+t2[8]);
          */
        
        svdReorder(m, t1, t2, scales, outRot, outScale);
        
    }
    
    static void svdReorder(double[] m, double[] t1, double[] t2, double[] scales, double[] outRot, double[] outScale) {
        
        int[] out = new int[3];
        int in0, in1, in2, index, i;
        double[] mag = new double[3];
        double[] rot = new double[9];
        
        // check for rotation information in the scales
        if (scales[0] < 0.0) { // move the rotation info to rotation matrix
            scales[0] = -scales[0];
            t2[0] = -t2[0];
            t2[1] = -t2[1];
            t2[2] = -t2[2];
        }
        if (scales[1] < 0.0) { // move the rotation info to rotation matrix
            scales[1] = -scales[1];
            t2[3] = -t2[3];
            t2[4] = -t2[4];
            t2[5] = -t2[5];
        }
        if (scales[2] < 0.0) { // move the rotation info to rotation matrix
            scales[2] = -scales[2];
            t2[6] = -t2[6];
            t2[7] = -t2[7];
            t2[8] = -t2[8];
        }
        
        mat_mul(t1, t2, rot);
        
        // check for equal scales case  and do not reorder
        if (almostEqual(Math.abs(scales[0]), Math.abs(scales[1])) && almostEqual(Math.abs(scales[1]), Math.abs(scales[2]))) {
            for (i = 0; i < 9; i++) {
                outRot[i] = rot[i];
            }
            for (i = 0; i < 3; i++) {
                outScale[i] = scales[i];
            }
            
        } else {
            
            // sort the order of the results of SVD
            if (scales[0] > scales[1]) {
                if (scales[0] > scales[2]) {
                    if (scales[2] > scales[1]) {
                        out[0] = 0;
                        out[1] = 2;
                        out[2] = 1; // xzy
                    } else {
                        out[0] = 0;
                        out[1] = 1;
                        out[2] = 2; // xyz
                    }
                } else {
                    out[0] = 2;
                    out[1] = 0;
                    out[2] = 1; // zxy
                }
            } else { // y > x
                if (scales[1] > scales[2]) {
                    if (scales[2] > scales[0]) {
                        out[0] = 1;
                        out[1] = 2;
                        out[2] = 0; // yzx
                    } else {
                        out[0] = 1;
                        out[1] = 0;
                        out[2] = 2; // yxz
                    }
                } else {
                    out[0] = 2;
                    out[1] = 1;
                    out[2] = 0; // zyx
                }
            }
            
            /*
            System.out.println("\nscales="+scales[0]+" "+scales[1]+" "+scales[2]);
            System.out.println("\nrot="+rot[0]+" "+rot[1]+" "+rot[2]);
            System.out.println("rot="+rot[3]+" "+rot[4]+" "+rot[5]);
            System.out.println("rot="+rot[6]+" "+rot[7]+" "+rot[8]);
            */
            
            // sort the order of the input matrix
            mag[0] = (m[0] * m[0] + m[1] * m[1] + m[2] * m[2]);
            mag[1] = (m[3] * m[3] + m[4] * m[4] + m[5] * m[5]);
            mag[2] = (m[6] * m[6] + m[7] * m[7] + m[8] * m[8]);
            
            if (mag[0] > mag[1]) {
                if (mag[0] > mag[2]) {
                    if (mag[2] > mag[1]) {
                        // 0 - 2 - 1
                        in0 = 0;
                        in2 = 1;
                        in1 = 2;// xzy
                    } else {
                        // 0 - 1 - 2
                        in0 = 0;
                        in1 = 1;
                        in2 = 2; // xyz
                    }
                } else {
                    // 2 - 0 - 1
                    in2 = 0;
                    in0 = 1;
                    in1 = 2; // zxy
                }
            } else { // y > x   1>0
                if (mag[1] > mag[2]) {
                    if (mag[2] > mag[0]) {
                        // 1 - 2 - 0
                        in1 = 0;
                        in2 = 1;
                        in0 = 2; // yzx
                    } else {
                        // 1 - 0 - 2
                        in1 = 0;
                        in0 = 1;
                        in2 = 2; // yxz
                    }
                } else {
                    // 2 - 1 - 0
                    in2 = 0;
                    in1 = 1;
                    in0 = 2; // zyx
                }
            }
            
            index = out[in0];
            outScale[0] = scales[index];
            
            index = out[in1];
            outScale[1] = scales[index];
            
            index = out[in2];
            outScale[2] = scales[index];
            
            index = out[in0];
            outRot[0] = rot[index];
            
            index = out[in0] + 3;
            outRot[3] = rot[index];
            
            index = out[in0] + 6;
            outRot[6] = rot[index];
            
            index = out[in1];
            outRot[1] = rot[index];
            
            index = out[in1] + 3;
            outRot[1 + 3] = rot[index];
            
            index = out[in1] + 6;
            outRot[1 + 6] = rot[index];
            
            index = out[in2];
            outRot[2] = rot[index];
            
            index = out[in2] + 3;
            outRot[2 + 3] = rot[index];
            
            index = out[in2] + 6;
            outRot[2 + 6] = rot[index];
        }
    }
    
    static int compute_qr(double[] s, double[] e, double[] u, double[] v) {
        
        int k;
        boolean converged;
        double shift, r;
        double[] cosl = new double[2];
        double[] cosr = new double[2];
        double[] sinl = new double[2];
        double[] sinr = new double[2];
        double[] m = new double[9];
        
        double utemp, vtemp;
        double f, g;
        
        final int MAX_INTERATIONS = 10;
        final double CONVERGE_TOL = 4.89E-15;
        
        double c_b48 = 1.;
        int first;
        converged = false;
        
        first = 1;
        
        if (Math.abs(e[1]) < CONVERGE_TOL || Math.abs(e[0]) < CONVERGE_TOL)
            converged = true;
        
        for (k = 0; k < MAX_INTERATIONS && !converged; k++) {
            shift = compute_shift(s[1], e[1], s[2]);
            f = (Math.abs(s[0]) - shift) * (d_sign(c_b48, s[0]) + shift / s[0]);
            g = e[0];
            r = compute_rot(f, g, sinr, cosr, 0, first);
            f = cosr[0] * s[0] + sinr[0] * e[0];
            e[0] = cosr[0] * e[0] - sinr[0] * s[0];
            g = sinr[0] * s[1];
            s[1] = cosr[0] * s[1];
            
            r = compute_rot(f, g, sinl, cosl, 0, first);
            first = 0;
            s[0] = r;
            f = cosl[0] * e[0] + sinl[0] * s[1];
            s[1] = cosl[0] * s[1] - sinl[0] * e[0];
            g = sinl[0] * e[1];
            e[1] = cosl[0] * e[1];
            
            r = compute_rot(f, g, sinr, cosr, 1, first);
            e[0] = r;
            f = cosr[1] * s[1] + sinr[1] * e[1];
            e[1] = cosr[1] * e[1] - sinr[1] * s[1];
            g = sinr[1] * s[2];
            s[2] = cosr[1] * s[2];
            
            r = compute_rot(f, g, sinl, cosl, 1, first);
            s[1] = r;
            f = cosl[1] * e[1] + sinl[1] * s[2];
            s[2] = cosl[1] * s[2] - sinl[1] * e[1];
            e[1] = f;
            
            // update u  matrices
            utemp = u[0];
            u[0] = cosl[0] * utemp + sinl[0] * u[3];
            u[3] = -sinl[0] * utemp + cosl[0] * u[3];
            utemp = u[1];
            u[1] = cosl[0] * utemp + sinl[0] * u[4];
            u[4] = -sinl[0] * utemp + cosl[0] * u[4];
            utemp = u[2];
            u[2] = cosl[0] * utemp + sinl[0] * u[5];
            u[5] = -sinl[0] * utemp + cosl[0] * u[5];
            
            utemp = u[3];
            u[3] = cosl[1] * utemp + sinl[1] * u[6];
            u[6] = -sinl[1] * utemp + cosl[1] * u[6];
            utemp = u[4];
            u[4] = cosl[1] * utemp + sinl[1] * u[7];
            u[7] = -sinl[1] * utemp + cosl[1] * u[7];
            utemp = u[5];
            u[5] = cosl[1] * utemp + sinl[1] * u[8];
            u[8] = -sinl[1] * utemp + cosl[1] * u[8];
            
            // update v  matrices
            
            vtemp = v[0];
            v[0] = cosr[0] * vtemp + sinr[0] * v[1];
            v[1] = -sinr[0] * vtemp + cosr[0] * v[1];
            vtemp = v[3];
            v[3] = cosr[0] * vtemp + sinr[0] * v[4];
            v[4] = -sinr[0] * vtemp + cosr[0] * v[4];
            vtemp = v[6];
            v[6] = cosr[0] * vtemp + sinr[0] * v[7];
            v[7] = -sinr[0] * vtemp + cosr[0] * v[7];
            
            vtemp = v[1];
            v[1] = cosr[1] * vtemp + sinr[1] * v[2];
            v[2] = -sinr[1] * vtemp + cosr[1] * v[2];
            vtemp = v[4];
            v[4] = cosr[1] * vtemp + sinr[1] * v[5];
            v[5] = -sinr[1] * vtemp + cosr[1] * v[5];
            vtemp = v[7];
            v[7] = cosr[1] * vtemp + sinr[1] * v[8];
            v[8] = -sinr[1] * vtemp + cosr[1] * v[8];
            
            m[0] = s[0];
            m[1] = e[0];
            m[2] = 0.0;
            m[3] = 0.0;
            m[4] = s[1];
            m[5] = e[1];
            m[6] = 0.0;
            m[7] = 0.0;
            m[8] = s[2];
            
            if (Math.abs(e[1]) < CONVERGE_TOL || Math.abs(e[0]) < CONVERGE_TOL)
                converged = true;
        }
        
        if (Math.abs(e[1]) < CONVERGE_TOL) {
            compute_2X2(s[0], e[0], s[1], s, sinl, cosl, sinr, cosr, 0);
            
            utemp = u[0];
            u[0] = cosl[0] * utemp + sinl[0] * u[3];
            u[3] = -sinl[0] * utemp + cosl[0] * u[3];
            utemp = u[1];
            u[1] = cosl[0] * utemp + sinl[0] * u[4];
            u[4] = -sinl[0] * utemp + cosl[0] * u[4];
            utemp = u[2];
            u[2] = cosl[0] * utemp + sinl[0] * u[5];
            u[5] = -sinl[0] * utemp + cosl[0] * u[5];
            
            // update v  matrices
            
            vtemp = v[0];
            v[0] = cosr[0] * vtemp + sinr[0] * v[1];
            v[1] = -sinr[0] * vtemp + cosr[0] * v[1];
            vtemp = v[3];
            v[3] = cosr[0] * vtemp + sinr[0] * v[4];
            v[4] = -sinr[0] * vtemp + cosr[0] * v[4];
            vtemp = v[6];
            v[6] = cosr[0] * vtemp + sinr[0] * v[7];
            v[7] = -sinr[0] * vtemp + cosr[0] * v[7];
        } else {
            compute_2X2(s[1], e[1], s[2], s, sinl, cosl, sinr, cosr, 1);
            
            utemp = u[3];
            u[3] = cosl[0] * utemp + sinl[0] * u[6];
            u[6] = -sinl[0] * utemp + cosl[0] * u[6];
            utemp = u[4];
            u[4] = cosl[0] * utemp + sinl[0] * u[7];
            u[7] = -sinl[0] * utemp + cosl[0] * u[7];
            utemp = u[5];
            u[5] = cosl[0] * utemp + sinl[0] * u[8];
            u[8] = -sinl[0] * utemp + cosl[0] * u[8];
            
            // update v  matrices
            
            vtemp = v[1];
            v[1] = cosr[0] * vtemp + sinr[0] * v[2];
            v[2] = -sinr[0] * vtemp + cosr[0] * v[2];
            vtemp = v[4];
            v[4] = cosr[0] * vtemp + sinr[0] * v[5];
            v[5] = -sinr[0] * vtemp + cosr[0] * v[5];
            vtemp = v[7];
            v[7] = cosr[0] * vtemp + sinr[0] * v[8];
            v[8] = -sinr[0] * vtemp + cosr[0] * v[8];
        }
        
        return (0);
    }
    
    static double max(double a, double b) {
        if (a > b)
            return (a);
        else
            return (b);
    }
    
    static double min(double a, double b) {
        if (a < b)
            return (a);
        else
            return (b);
    }
    
    static double d_sign(double a, double b) {
        double x;
        x = (a >= 0 ? a : -a);
        return (b >= 0 ? x : -x);
    }
    
    static double compute_shift(double f, double g, double h) {
        double d__1, d__2;
        double fhmn, fhmx, c, fa, ga, ha, as, at, au;
        double ssmin;
        
        fa = Math.abs(f);
        ga = Math.abs(g);
        ha = Math.abs(h);
        fhmn = min(fa, ha);
        fhmx = max(fa, ha);
        if (fhmn == 0.) {
            ssmin = 0.;
            if (fhmx == 0.) {} else {
                d__1 = min(fhmx, ga) / max(fhmx, ga);
            }
        } else {
            if (ga < fhmx) {
                as = fhmn / fhmx + 1.;
                at = (fhmx - fhmn) / fhmx;
                d__1 = ga / fhmx;
                au = d__1 * d__1;
                c = 2. / (Math.sqrt(as * as + au) + Math.sqrt(at * at + au));
                ssmin = fhmn * c;
            } else {
                au = fhmx / ga;
                if (au == 0.) {
                    ssmin = fhmn * fhmx / ga;
                } else {
                    as = fhmn / fhmx + 1.;
                    at = (fhmx - fhmn) / fhmx;
                    d__1 = as * au;
                    d__2 = at * au;
                    c = 1. / (Math.sqrt(d__1 * d__1 + 1.) + Math.sqrt(d__2 * d__2 + 1.));
                    ssmin = fhmn * c * au;
                    ssmin += ssmin;
                }
            }
        }
        
        return (ssmin);
    }
    
    static int compute_2X2(double f, double g, double h, double[] single_values, double[] snl, double[] csl, double[] snr, double[] csr, int index) {
        
        double c_b3 = 2.;
        double c_b4 = 1.;
        
        double d__1;
        int pmax;
        double temp;
        boolean swap;
        double a, d, l, m, r, s, t, tsign, fa, ga, ha;
        double ft, gt, ht, mm;
        boolean gasmal;
        double tt, clt, crt, slt, srt;
        double ssmin, ssmax;
        
        ssmax = single_values[0];
        ssmin = single_values[1];
        clt = 0.0;
        crt = 0.0;
        slt = 0.0;
        srt = 0.0;
        tsign = 0.0;
        
        ft = f;
        fa = Math.abs(ft);
        ht = h;
        ha = Math.abs(h);
        
        pmax = 1;
        swap = ha > fa;
        
        if (swap) {
            pmax = 3;
            temp = ft;
            ft = ht;
            ht = temp;
            temp = fa;
            fa = ha;
            ha = temp;
            
        }
        gt = g;
        ga = Math.abs(gt);
        if (ga == 0.) {
            
            single_values[1] = ha;
            single_values[0] = fa;
            clt = 1.;
            crt = 1.;
            slt = 0.;
            srt = 0.;
        } else {
            gasmal = true;
            
            if (ga > fa) {
                pmax = 2;
                if (fa / ga < EPS) {
                    
                    gasmal = false;
                    ssmax = ga;
                    if (ha > 1.) {
                        ssmin = fa / (ga / ha);
                    } else {
                        ssmin = fa / ga * ha;
                    }
                    clt = 1.;
                    slt = ht / gt;
                    srt = 1.;
                    crt = ft / gt;
                }
            }
            if (gasmal) {
                
                d = fa - ha;
                if (d == fa) {
                    
                    l = 1.;
                } else {
                    l = d / fa;
                }
                
                m = gt / ft;
                
                t = 2. - l;
                
                mm = m * m;
                tt = t * t;
                s = Math.sqrt(tt + mm);
                
                if (l == 0.) {
                    r = Math.abs(m);
                } else {
                    r = Math.sqrt(l * l + mm);
                }
                
                a = (s + r) * .5;
                
                if (ga > fa) {
                    pmax = 2;
                    if (fa / ga < EPS) {
                        
                        gasmal = false;
                        ssmax = ga;
                        if (ha > 1.) {
                            ssmin = fa / (ga / ha);
                        } else {
                            ssmin = fa / ga * ha;
                        }
                        clt = 1.;
                        slt = ht / gt;
                        srt = 1.;
                        crt = ft / gt;
                    }
                }
                if (gasmal) {
                    
                    d = fa - ha;
                    if (d == fa) {
                        
                        l = 1.;
                    } else {
                        l = d / fa;
                    }
                    
                    m = gt / ft;
                    
                    t = 2. - l;
                    
                    mm = m * m;
                    tt = t * t;
                    s = Math.sqrt(tt + mm);
                    
                    if (l == 0.) {
                        r = Math.abs(m);
                    } else {
                        r = Math.sqrt(l * l + mm);
                    }
                    
                    a = (s + r) * .5;
                    
                    ssmin = ha / a;
                    ssmax = fa * a;
                    if (mm == 0.) {
                        
                        if (l == 0.) {
                            t = d_sign(c_b3, ft) * d_sign(c_b4, gt);
                        } else {
                            t = gt / d_sign(d, ft) + m / t;
                        }
                    } else {
                        t = (m / (s + t) + m / (r + l)) * (a + 1.);
                    }
                    l = Math.sqrt(t * t + 4.);
                    crt = 2. / l;
                    srt = t / l;
                    clt = (crt + srt * m) / a;
                    slt = ht / ft * srt / a;
                }
            }
            if (swap) {
                csl[0] = srt;
                snl[0] = crt;
                csr[0] = slt;
                snr[0] = clt;
            } else {
                csl[0] = clt;
                snl[0] = slt;
                csr[0] = crt;
                snr[0] = srt;
            }
            
            if (pmax == 1) {
                tsign = d_sign(c_b4, csr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, f);
            }
            if (pmax == 2) {
                tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, csl[0]) * d_sign(c_b4, g);
            }
            if (pmax == 3) {
                tsign = d_sign(c_b4, snr[0]) * d_sign(c_b4, snl[0]) * d_sign(c_b4, h);
            }
            single_values[index] = d_sign(ssmax, tsign);
            d__1 = tsign * d_sign(c_b4, f) * d_sign(c_b4, h);
            single_values[index + 1] = d_sign(ssmin, d__1);
            
        }
        return 0;
    }
    
    static double compute_rot(double f, double g, double[] sin, double[] cos, int index, int first) {
        double cs, sn;
        int i;
        double scale;
        int count;
        double f1, g1;
        double r;
        final double safmn2 = 2.002083095183101E-146;
        final double safmx2 = 4.994797680505588E+145;
        
        if (g == 0.) {
            cs = 1.;
            sn = 0.;
            r = f;
        } else if (f == 0.) {
            cs = 0.;
            sn = 1.;
            r = g;
        } else {
            f1 = f;
            g1 = g;
            scale = max(Math.abs(f1), Math.abs(g1));
            if (scale >= safmx2) {
                count = 0;
                while (scale >= safmx2) {
                    ++count;
                    f1 *= safmn2;
                    g1 *= safmn2;
                    scale = max(Math.abs(f1), Math.abs(g1));
                }
                r = Math.sqrt(f1 * f1 + g1 * g1);
                cs = f1 / r;
                sn = g1 / r;
                for (i = 1; i <= count; ++i) {
                    r *= safmx2;
                }
            } else if (scale <= safmn2) {
                count = 0;
                while (scale <= safmn2) {
                    ++count;
                    f1 *= safmx2;
                    g1 *= safmx2;
                    scale = max(Math.abs(f1), Math.abs(g1));
                }
                r = Math.sqrt(f1 * f1 + g1 * g1);
                cs = f1 / r;
                sn = g1 / r;
                for (i = 1; i <= count; ++i) {
                    r *= safmn2;
                }
            } else {
                r = Math.sqrt(f1 * f1 + g1 * g1);
                cs = f1 / r;
                sn = g1 / r;
            }
            if (Math.abs(f) > Math.abs(g) && cs < 0.) {
                cs = -cs;
                sn = -sn;
                r = -r;
            }
        }
        sin[index] = sn;
        cos[index] = cs;
        return r;
        
    }
    
    static void print_mat(double[] mat) {
        int i;
        for (i = 0; i < 3; i++) {
            System.out.println(mat[i * 3] + " " + mat[i * 3 + 1] + " " + mat[i * 3 + 2] + "\n");
        }
        
    }
    
    static void print_det(double[] mat) {
        double det;
        
        det = mat[0] * mat[4] * mat[8] + mat[1] * mat[5] * mat[6] + mat[2] * mat[3] * mat[7] - mat[2] * mat[4] * mat[6] - mat[0] * mat[5] * mat[7] - mat[1] * mat[3] * mat[8];
        System.out.println("det= " + det);
    }
    
    static void mat_mul(double[] m1, double[] m2, double[] m3) {
        int i;
        double[] tmp = new double[9];
        
        tmp[0] = m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6];
        tmp[1] = m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7];
        tmp[2] = m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8];
        
        tmp[3] = m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6];
        tmp[4] = m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7];
        tmp[5] = m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8];
        
        tmp[6] = m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6];
        tmp[7] = m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7];
        tmp[8] = m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8];
        
        for (i = 0; i < 9; i++) {
            m3[i] = tmp[i];
        }
    }
    
    static void transpose_mat(double[] in, double[] out) {
        out[0] = in[0];
        out[1] = in[3];
        out[2] = in[6];
        
        out[3] = in[1];
        out[4] = in[4];
        out[5] = in[7];
        
        out[6] = in[2];
        out[7] = in[5];
        out[8] = in[8];
    }
    
    static double max3(double[] values) {
        if (values[0] > values[1]) {
            if (values[0] > values[2])
                return (values[0]);
            else
                return (values[2]);
        } else {
            if (values[1] > values[2])
                return (values[1]);
            else
                return (values[2]);
        }
    }
    
    private static boolean almostEqual(double a, double b) {
        if (a == b)
            return true;
        
        final double EPSILON_ABSOLUTE = 1.0e-6;
        final double EPSILON_RELATIVE = 1.0e-4;
        double diff = Math.abs(a - b);
        double absA = Math.abs(a);
        double absB = Math.abs(b);
        double max = (absA >= absB) ? absA : absB;
        
        if (diff < EPSILON_ABSOLUTE)
            return true;
        
        return (diff / max) < EPSILON_RELATIVE;
    }
    
}
