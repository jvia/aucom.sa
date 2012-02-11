// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.3.1

package updater;

public final class UpdateMessagePrxHelper extends Ice.ObjectPrxHelperBase implements UpdateMessagePrx
{
    public static UpdateMessagePrx
    checkedCast(Ice.ObjectPrx __obj)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (UpdateMessagePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::updater::UpdateMessage"))
                {
                    UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UpdateMessagePrx
    checkedCast(Ice.ObjectPrx __obj, java.util.Map<String, String> __ctx)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (UpdateMessagePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                if(__obj.ice_isA("::updater::UpdateMessage", __ctx))
                {
                    UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
                    __h.__copyFrom(__obj);
                    __d = __h;
                }
            }
        }
        return __d;
    }

    public static UpdateMessagePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::updater::UpdateMessage"))
                {
                    UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static UpdateMessagePrx
    checkedCast(Ice.ObjectPrx __obj, String __facet, java.util.Map<String, String> __ctx)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            try
            {
                if(__bb.ice_isA("::updater::UpdateMessage", __ctx))
                {
                    UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
                    __h.__copyFrom(__bb);
                    __d = __h;
                }
            }
            catch(Ice.FacetNotExistException ex)
            {
            }
        }
        return __d;
    }

    public static UpdateMessagePrx
    uncheckedCast(Ice.ObjectPrx __obj)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            try
            {
                __d = (UpdateMessagePrx)__obj;
            }
            catch(ClassCastException ex)
            {
                UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
                __h.__copyFrom(__obj);
                __d = __h;
            }
        }
        return __d;
    }

    public static UpdateMessagePrx
    uncheckedCast(Ice.ObjectPrx __obj, String __facet)
    {
        UpdateMessagePrx __d = null;
        if(__obj != null)
        {
            Ice.ObjectPrx __bb = __obj.ice_facet(__facet);
            UpdateMessagePrxHelper __h = new UpdateMessagePrxHelper();
            __h.__copyFrom(__bb);
            __d = __h;
        }
        return __d;
    }

    protected Ice._ObjectDelM
    __createDelegateM()
    {
        return new _UpdateMessageDelM();
    }

    protected Ice._ObjectDelD
    __createDelegateD()
    {
        return new _UpdateMessageDelD();
    }

    public static void
    __write(IceInternal.BasicStream __os, UpdateMessagePrx v)
    {
        __os.writeProxy(v);
    }

    public static UpdateMessagePrx
    __read(IceInternal.BasicStream __is)
    {
        Ice.ObjectPrx proxy = __is.readProxy();
        if(proxy != null)
        {
            UpdateMessagePrxHelper result = new UpdateMessagePrxHelper();
            result.__copyFrom(proxy);
            return result;
        }
        return null;
    }
}
