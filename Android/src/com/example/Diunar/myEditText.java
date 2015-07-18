package com.example.Diunar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.EditText;

public class myEditText extends EditText{

  private int lineColor;//������ɫ
  private float lineWidth;//���߿��

  public myEditText(Context context) {
    super(context);

    //����Ĭ����ɫ�ͺ��߿��
    lineColor = Color.BLACK;
    lineWidth = 0.8f;//Ĭ�Ͽ��Ϊ0.5
  }

  public myEditText(Context context,int color,float width) {
    super(context);

    //������ɫ�ͺ��߿��
    this.lineColor = color;
    this.lineWidth = width;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    // TODO Auto-generated method stub
    super.onDraw(canvas);

    //��������
    Paint mPaint = new Paint();
    mPaint.setStrokeWidth(lineWidth);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setColor(lineColor);

    //��ȡ����
    int padL = this.getPaddingLeft();//��ȡ�����������
    int padR = this.getPaddingRight();//��ȡ�����ұ�����
    int padT = this.getPaddingTop();//��ȡ���ڶ�������
    int lines = this.getLineCount();//��ȡ����
    float size = this.getTextSize();//��ȡ�����С
    float baseTop = padT + size / 6;//�������µ�һ���ߵ�λ��
    /*������Ҫ˵������size/6���ֵ����żȻ���Եõ��ģ��������о��һ��
     *Ϊʲô����EditText.getLineSpacingExtra();����ȡ�оࣿ
     *��Ϊ���Է���������EditText��getLineSpacingExtra�����ᱨNoSuchMethod���󣬾���ԭ����
     *���Է����о��ֵ������TextSize��1/3������Ҫ�õ��о��ʱ����������ֵ������getLineSpacingExtra����
     * */
    float gap = this.getLineHeight();//��ȡ�п�
    
    //�������»���
    for(int i = 1;i <= lines;i++)
    {
      canvas.drawLine(padL//startX
          , baseTop + gap * i//startY
          , this.getWidth() - padR//endX
          , baseTop + gap * i//endY
          , mPaint);
    }
  }

  public int getLineColor() {
    return lineColor;
  }

  public void setLineColor(int color) {
    this.lineColor = color;
  }

  public float getLineWidth() {
    return lineWidth;
  }

  public void setLineWidth(float width) {
    this.lineWidth = width;
  }

}
