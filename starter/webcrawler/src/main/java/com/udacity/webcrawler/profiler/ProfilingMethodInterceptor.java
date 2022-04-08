package com.udacity.webcrawler.profiler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A method interceptor that checks whether {@link Method}s are annotated with the {@link Profiled}
 * annotation. If they are, the method interceptor records how long the method invocation took.
 */
final class ProfilingMethodInterceptor implements InvocationHandler {

  private final Clock clock;
  private final  Object profiledObject;
  private final ProfilingState state;

  // TODO: You will need to add more instance fields and constructor arguments to this class.
  ProfilingMethodInterceptor(Clock clock, Object profiledObject, ProfilingState state) {
    this.clock = Objects.requireNonNull(clock);
    this.profiledObject = profiledObject;
    this.state = state;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // TODO: This method interceptor should inspect the called method to see if it is a profiled
    //       method. For profiled methods, the interceptor should record the start time, then
    //       invoke the method using the object that is being profiled. Finally, for profiled
    //       methods, the interceptor should record how long the method call took, using the
    //       ProfilingState methods.
    final Instant startTime = clock.instant();
    if (method.getDeclaringClass().equals(Object.class)) {
      // Invoke toString() and hashCode() directly on the property Map.
      try {
        return method.invoke(profiledObject, args);
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }finally {
        state.record(profiledObject.getClass(),method, Duration.between(startTime,clock.instant()));
      }
    }
    if(method.getAnnotation(Profiled.class) != null){
      try {
        return method.invoke(profiledObject, args);
      } catch (InvocationTargetException e) {
        throw e.getTargetException();
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }finally {
        state.record(profiledObject.getClass(),method, Duration.between(startTime,clock.instant()));
      }

    }
    return method.invoke(profiledObject, args);
  }
}
