/**
 * Copyright 2013 Dennis Ippel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ygame.math.vector;

/**
 * Encapsulates a 3D point/vector.
 * 
 * This class borrows heavily from the
 * implementation.
 * 
 * @see <a
 *      href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Vector3.java">libGDX->Vector3</a>
 * 
 *      This class is not thread safe and must be
 *      confined to a single thread or protected by
 *      some external locking mechanism if necessary.
 *      All static methods are thread safe.
 * 
 * @author dennis.ippel
 * @author Jared Woolston (jwoolston@tenkiv.com)
 * @author Dominic Cerisano (Gram-Schmidt
 *         orthonormalization)
 */
public class Vector3
{
	// The vector components
	public float x;
	public float y;
	public float z;

	// Unit vectors oriented to each axis
	// DO NOT EVER MODIFY THE VALUES OF THESE
	// MEMBERS
	/**
	 * DO NOT EVER MODIFY THE VALUES OF THIS
	 * VECTOR
	 */
	public static final Vector3 X = new Vector3(1, 0, 0);
	/**
	 * DO NOT EVER MODIFY THE VALUES OF THIS
	 * VECTOR
	 */
	public static final Vector3 Y = new Vector3(0, 1, 0);
	/**
	 * DO NOT EVER MODIFY THE VALUES OF THIS
	 * VECTOR
	 */
	public static final Vector3 Z = new Vector3(0, 0, 1);

	/**
	 * Constructs a new {@link Vector3} at (0, 0,
	 * 0).
	 */
	public Vector3()
	{
		// They are technically zero, but we
		// wont rely on the uninitialized
		// state here.
		x = 0;
		y = 0;
		z = 0;
	}

	/**
	 * Constructs a new {@link Vector3} at {from,
	 * from, from}.
	 * 
	 * @param from
	 *                float which all components
	 *                will be initialized to.
	 */
	public Vector3(float from)
	{
		x = from;
		y = from;
		z = from;
	}

	/**
	 * Constructs a new {@link Vector3} with
	 * components matching the input
	 * {@link Vector3}.
	 * 
	 * @param from
	 *                {@link Vector3} to
	 *                initialize the components
	 *                with.
	 */
	public Vector3(final Vector3 from)
	{
		x = from.x;
		y = from.y;
		z = from.z;
	}

	/**
	 * Constructs a new {@link Vector3} object
	 * with components initialized to the
	 * specified values.
	 * 
	 * @param x
	 *                float The x component.
	 * @param y
	 *                float The y component.
	 * @param z
	 *                float The z component.
	 */
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Sets all components of this
	 * {@link Vector3} to the specified values.
	 * 
	 * @param x
	 *                float The x component.
	 * @param y
	 *                float The y component.
	 * @param z
	 *                float The z component.
	 * @return A reference to this
	 *         {@link Vector3} to facilitate
	 *         chaining.
	 */
	public Vector3 setAll(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Sets all components of this
	 * {@link Vector3} to the values provided by
	 * the input {@link Vector3}.
	 * 
	 * @param other
	 *                {@link Vector3} The vector
	 *                to copy.
	 * @return A reference to this
	 *         {@link Vector3} to facilitate
	 *         chaining.
	 */
	public Vector3 setAll(Vector3 other)
	{
		x = other.x;
		y = other.y;
		z = other.z;
		return this;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3 other = (Vector3) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Vector3 <x, y, z>: <").append(x).append(", ")
				.append(y).append(", ").append(z).append(">");
		return sb.toString();
	}
}
