#include<iostream>
#include<fstream>
#include<cstdlib>
#include<ctime>
#include<cstring>
using namespace std;
string a[10000005];
int shuffle(int length){
    return rand() % length;
}
int main(){
    string flower[18] = {"paeoniaSA", "paeoniaLP", "camellia", "chrysanthemum", "plum", "orchid",
                         "rose", "azalea", "tulip", "jasmine", "begonia", "lotus",
                         "gardenia", "lotus", "lily", "carnation", "rose", "gesang"};
    int freq[18] = {6, 5, 5, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 2, 2, 1, 1, 1};
    srand(time(0));
    ofstream fout("data.txt");
    int n;
    cin>>n;
    for(int i = 0; i < 18; i++){
        for(int j = 0; j < (n * freq[i] / 53); j++){
            int t = shuffle(n);
            while(a[t] != ""){
                t = shuffle(n);
            }
            a[t] = flower[i];
        }
    }
    for(int i = 0; i < n; i++){
        fout<<a[i]<<" ";
    }
    fout.close();
    return 0;
}