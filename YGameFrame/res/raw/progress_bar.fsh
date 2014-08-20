precision mediump float;

const vec4 backColor = vec4(0.0 , 1.0 , 0.0 , 1.0);
const vec4 foreColor = vec4(1.0 , 0.0 , 0 , 1.0);

uniform float progress;

varying vec2 vTextureCoord;

void main()
{
		float weight = step(progress , vTextureCoord.x);
		//float weight = smoothstep(progress - 0.1,progress +0.1, vTextureCoord.x);

		gl_FragColor = mix(foreColor , backColor , weight);
}