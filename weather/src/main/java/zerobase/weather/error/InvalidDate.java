package zerobase.weather.error;
/*
유효하지 않은 날짜를 받았을 때 예외처리 IvalidDate
 클래스는 RuntimeException 클래스를 상속받고 있습니다.
RuntimeException 클래스는 예외가 발생했을 때 처리하지 않아도 되는
 unchecked 예외 클래스입니다. 이 클래스를 상속받는 이유는,
 InvalidDate 예외가 발생했을 때 프로그램이 중단되는 것을
 방지하기 위함입니다.
 * **/
public class InvalidDate extends RuntimeException{
    // private static final로 선언되었기 때문에 클래스 내부에서만 사용 가능합니다.
    private static final String Message = "너무 과거 혹은 미래의 날짜입니다.";

    // InvalidDate 클래스의 생성자는 Message를 인자로 받지 않고,
    // super 키워드를 사용하여 부모 클래스인 RuntimeException
    // 클래스의 생성자를 호출합니다. 이때 인자로 Message 상수를
    // 전달하고 있습니다. 따라서 InvalidDate 예외가 발생했을 때
    // "너무 과거 혹은 미래의 날짜입니다." 라는 메시지가 출력됩니다.
    public InvalidDate(){
        super(Message);
    }
}


/*super 키워드는 부모 클래스의 생성자를 호출하기 위해 사용됩니다.
 * 부모 클래스의 생성자를 호출하면, 부모 클래스의 필드나 메소드를
 * 사용할 수 있게 됩니다. 따라서 InvalidDate 클래스에서
 * super(Message)를 호출하면 RuntimeException 클래스의 생성자를
 * 호출하면서, 예외 메시지를 설정할 수 있습니다.
 */
