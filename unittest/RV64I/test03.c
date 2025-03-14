int main(void){
    int a =0;
    int b =10;
    int c = a >> 5;
    unsigned int d = 20;
    int e = -10;
    if(c == a){
        d=3;
    }else{
        d=7;
    }
    for(int i=0;i<=d;i++){
        e+=i;
    }
    return 0;
}