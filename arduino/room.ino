#include <SoftwareSerial.h> 

//Serial port for bluetooth
SoftwareSerial btm(2,3);

//Pins for appliances and IR sensors
int light = 13;
int fan= 12;
int ir1 = 8;
int ir2 =9;

int fstate=HIGH;
int lstate=HIGH;

char d;
int d1=0;
int d2=0;

//Count of ppl in room
int count = 1;

void setup() {
	//initial setup
  pinMode(ir1,INPUT);
  pinMode(ir2,INPUT);
  pinMode(light,OUTPUT);
  digitalWrite(light,HIGH);
  pinMode(fan,OUTPUT);
  digitalWrite(fan,HIGH);
  Serial.begin(9600);
  btm.begin(9600);
}

void loop() {

	//logic to detect person entering / leaving
  d1 = digitalRead(ir1);
  d2 = digitalRead(ir2);
  if(d1==1)
  {
    for(int i=0;i<600;i++)
    {
      d2 = digitalRead(ir2);
      if(d2==1)
      {
        count++;
        delay(1000);
        digitalWrite(light,HIGH);
        digitalWrite(fan,HIGH);
        break;
      }
      delay(5);
    }
  }
  else if(d2==1)
  {
    for(int i=0;i<600;i++)
    {
      d1 = digitalRead(ir1);
      if(d1==1)
      {
        count--;
        delay(2000);
        if(count<=0)
        {
          digitalWrite(light,LOW);
          digitalWrite(fan,LOW);
        }
        break;
      }
      delay(5);
    }
  }
  if(count<0)
  {
    count=0;
  }
  
  //Functions on Android app input 
  if(btm.available()>0)
  {
    d=btm.read();
    if(d==1)
    {
      digitalWrite(light,HIGH);
    }
    else if(d==0)
    {
      digitalWrite(light,LOW);
    }
  }
  delay(100);
  //Serial.println(count);
}
