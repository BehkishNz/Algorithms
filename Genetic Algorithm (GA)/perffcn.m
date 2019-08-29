function [ISE,t_r,t_s,M_p] = Q2_perfFCN(x)
%x = [3.33607834473209; 6.38183324372390; 2.29132095142772];
Kp = x(1,1);
Ti = x(2,1);
Td = x(3,1);

G = Kp*tf([Ti*Td,Ti,1],[Ti,0]);

F = tf(1,[1,6,11,6,0]);
sys = feedback(series(G,F),1);
sysinf = stepinfo(sys);
t = 0:0.01:100;
y = step(sys,t);

ISE = sum((y-1).^2);
t_r = sysinf.RiseTime;
t_s = sysinf.SettlingTime;
M_p = sysinf.Overshoot;


