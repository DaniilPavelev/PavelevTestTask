package com.example.pavelevtesttask;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;

public class Helper {
    //Метод добавляющий подходящие объекты в ObservableList
    public static void addToArrayTelemetricDate(ObservableList observableList, int countPackagesInTable){
        try {
            DatagramSocket aSocet = new DatagramSocket(15000);   //Объявление и инициализация необходимых элементов
            int startIndex;
            int countPackages =0;
            byte[] fullBuffer = GetBuffer(aSocet);
            byte[] packeg;
            int counterBefore=0;
            int counterAfter;
            while(countPackages<countPackagesInTable){              //Запуск цикла до достижения определенного количества извлеченных пакетов
                startIndex = FindIndexStart(fullBuffer);            //Проверка на возможность наличия синхромаркера
                if(startIndex==-1){
                    byte[] buffer;
                    buffer = GetBuffer(aSocet);
                    fullBuffer = ArrayUtils.addAll(fullBuffer,buffer);
                }
                else {
                    byte[] buffer;
                    buffer = GetBuffer(aSocet);
                    fullBuffer = ArrayUtils.addAll(fullBuffer,buffer);
                    if(CheckStart(fullBuffer, startIndex)){             //Проверка на наличие синхромаркера
                        packeg = ExtractPackeg(fullBuffer,startIndex);  //Извлечение пакета
                        fullBuffer = DeleteAnother(fullBuffer, startIndex);
                        if(getCounter(packeg)<1000000&&getCounter(packeg)>-10000000){ //Проверка на последовательность счётчика пакетов
                            if(countPackages==0){                                     //
                                counterBefore = getCounter(packeg);                   //
                                counterAfter = counterBefore+1;                       //
                            }                                                         //
                            else counterAfter = getCounter(packeg);                   //
                            countPackages++;                                          //
                            if(counterAfter == counterBefore+1) {                     //
                                counterBefore = getCounter(packeg);                   //Проверка на последовательность счётчика пакетов
                                if (CheckKS(packeg)) {                  //Проверка контрольных сумм
                                    observableList.add(new TelemetricDate(          //Добавление подходящего элемента в ObservableList
                                            getSinhromarker(packeg),
                                            getCounter(packeg),
                                            getTime(packeg),
                                            getDate(packeg),
                                            getCRC(packeg)));
                                }
                            }
                        }
                    }
                    else fullBuffer = DeleteDo(fullBuffer,startIndex);
                }
            }
            aSocet.close();
        }
        catch (SocketException e){
            System.out.println(e.getMessage());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    //Метод возвращающий синхромаркер
    public static int getSinhromarker(byte[] packag){
        return ByteToInt(packag, 0, 4);
    }

    //Метод возвращающий счётчик
    public static int getCounter(byte[] packag){
        return ByteToInt(packag, 4, 4);
    }

    //Метод возвращающий время в секундах
    public static double getTime(byte[] packag){
        return ByteToDouble(packag, 8, 8);
    }

    //Метод возвращающий полезную информацию
    public static double getDate(byte[] packag){
        MathContext context = new MathContext(4, RoundingMode.HALF_UP);
        double a = ByteToDouble(packag, 16, 8);
        BigDecimal result = new BigDecimal(a,context);

        return result.doubleValue();
    }

    //Метод возвращающий контрольную сумму
    public static int getCRC(byte[] packag){
        return ByteToShort(packag, 24, 2);
    }

    //Метод сравнивающий контрольные суммы отправителя и получателя
    public static boolean CheckKS(byte[] packeg){
        return String.format("%08x", CalculateCrc16(packeg, 0, 24))
                .equals(String.format("%08x", ByteToShort(packeg, 24, 2)));
    }

    //Метод возвращающий 25 байт информации поступающей от сервера
    public static byte[] GetBuffer(DatagramSocket aSocet) throws IOException {
        byte[] buffer = new byte[25];
        DatagramPacket startReply = new DatagramPacket(buffer, buffer.length);
        aSocet.receive(startReply);
        return buffer;
    }

    //Метод рассчитывающий контрольную сумму CRC16_CCITT-FALSE
    public static int CalculateCrc16 (byte[] data, int offset, int length) {
        if (data == null || offset < 0 || offset > data.length - 1 || offset + length > data.length) {
            return 0;
        }
        int crc = 0xFFFF;
        for (int i = 0; i < length; ++i) {
            crc ^= data[offset + i] << 8;
            for (int j = 0; j < 8; ++j) {
                crc = (crc & 0x8000) > 0 ? (crc << 1) ^ 0x1021 : crc << 1;
            }
        }
        return crc & 0xFFFF;
    }

    //Метод копирующий массив начаня со startIndex
    public static byte[] CopyArr(byte[] bytes, int startIndex, int lenght){
        byte[] sinh = new byte[lenght];
        for (int i = 0; i<lenght;i++){
            sinh[i] = bytes[startIndex+i];
        }
        return sinh;
    }

    //Метод определяющий найден ли синхромаркер
    public static boolean CheckStart(byte[] bytes,int startIndex){
        byte[] sinh = new byte[4];
        for (int i = 0; i<sinh.length;i++){
            sinh[i] = bytes[startIndex+i];
        }
        return ByteToInt(sinh)==305419896;
    }

    //Метод определяющий может ли в массиве содержаться синхромаркер
    public  static int FindIndexStart(byte[] bytes){
        for(int i=0;i<bytes.length;i++){
            if(bytes[i]==120) return i;
        }
        return -1;
    }

    //Метод возвращающий int из массива байт
    public static int ByteToInt(byte[] bytes){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        ArrayUtils.reverse(bytes);
        buffer.put(bytes);
        buffer.rewind();
        return buffer.getInt();
    }

    //Метод возвращающий double из массива байт
    public static double ByteToDouble(byte[] bytes, int startIndex, int count){
        long accum = 0;
        byte[] temp = new byte[count];
        for (int i = 0; i < count; i++) {
            temp[i] = bytes[i+startIndex];
        }
        int i = 0;
        for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
            accum |= ( (long)( temp[i] & 0xff ) ) << shiftBy;
            i++;
        }

        return Double.longBitsToDouble(accum);
    }

    //Метод возвращающий int из массива байт
    public static int ByteToInt(byte[] bytes, int startIndex, int lenght){
        byte[] temp = new byte[lenght];
        for (int i = 0; i<lenght;i++){
            temp[i] = bytes[startIndex+i];
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        ArrayUtils.reverse(temp);
        buffer.put(temp);
        buffer.rewind();
        int a = buffer.getInt();
        return a;
    }

    //Метод возвращающий short из массива байт
    public static short ByteToShort(byte[] bytes, int startIndex, int lenght){
        byte[] temp = new byte[lenght];
        for (int i = 0; i<lenght;i++){
            temp[i] = bytes[startIndex+i];
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        ArrayUtils.reverse(temp);
        buffer.put(temp);
        buffer.rewind();
        return buffer.getShort();
    }

    //Метод удаляющий лишние остатки после извлечения пакета
    public static byte[] DeleteAnother(byte[] bytes, int startIndex){
        byte[] temp = new byte[bytes.length-startIndex];
        byte[] temp2 = new byte[temp.length-26];
        for(int i=0; i<temp.length;i++){
            temp[i]=bytes[startIndex+i];
        }
        bytes = temp;
        for(int i = 0;i<temp2.length;i++){
            temp2[i] = bytes[i+26];
        }
        bytes = temp2;
        return bytes;
    }

    //Метод удаляющий лишние остатки после извлечения пакета
    public static byte[] DeleteDo(byte[] bytes, int startIndex){
        byte[] temp = new byte[bytes.length-startIndex-1];
        for(int i=0; i<temp.length;i++){
            temp[i]=bytes[startIndex+i+1];
        }
        bytes = temp;
        return bytes;
    }

    //Метод извлекающий пакет
    public static byte[] ExtractPackeg(byte[] bytes, int startIndex){
        byte[] date = new byte[26];
        for (int i=0;i<date.length;i++){
            date[i] = bytes[i];
        }
        return date;
    }
}

